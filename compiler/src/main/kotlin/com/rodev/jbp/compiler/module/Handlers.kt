package com.rodev.jbp.compiler.module

import com.rodev.jbp.compiler.Actions
import com.rodev.jbp.compiler.json.put
import com.rodev.jbp.compiler.module.action.CodeAction
import com.rodev.jbp.compiler.module.action.CodeBasicAction
import com.rodev.jbp.compiler.module.action.CodeContainingAction
import com.rodev.jbp.compiler.module.handler.CodeFunction
import com.rodev.jbp.compiler.module.handler.CodeHandler
import com.rodev.jbp.compiler.module.handler.CodeProcess
import com.rodev.jbp.compiler.module.value.constants.TextConstant
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import java.nio.file.Files.walk

class Handlers {

    private var values = mutableListOf<CodeHandler>()
    private var built: Boolean = false

    operator fun plusAssign(codeHandlers: List<CodeHandler>) {
        values += codeHandlers
    }

    private fun build() {
        if (built) return

        built = true

        var functionId = 0
        var position = 0

        val result = values.toMutableList()
        val iterator = result.listIterator()

        while (iterator.hasNext()) {
            val handler = iterator.next()
            if (handler.length == 0 && !(handler is CodeFunction || handler is CodeProcess)) {
                // Concurrent Modification Exception
                iterator.remove()
                continue
            }

            handler.position = position++

            if (handler.length <= CodeHandler.MAX_LENGTH) continue

            var idx = 0

            with(BuilderScope(idx, functionId, result)){
                walk(handler)
                idx = this.idx
                functionId = this.functionId
            }
        }

        values = result
    }

    private fun BuilderScope.walk(container: CodeContainer, maxLength: Int = CodeHandler.MAX_LENGTH) {
        for (actionIdx in container.actions.indices) {
            val action = container.actions[actionIdx]
            val isContainer = action is CodeContainingAction
            val actionLength = if (isContainer) CodeContainingAction.LENGTH else CodeAction.LENGTH
            val newIdx = idx + actionLength
            val next = container.actions.getOrNull(actionIdx + 1)
            val hasNext = next != null
            val hasContents = isContainer && (action as CodeContainingAction).actions.size > 0

            val reserved = with(Unit) {
                var reserved = 0
                if (hasNext) reserved++

                if (!hasNext) return@with reserved
                if (!isContainer) return@with reserved
                if (next?.id != "else") return@with reserved
                if (next !is CodeContainingAction) return@with reserved

                reserved += 2
                if (next.actions.size > 0) reserved++
                container.actions.getOrNull(actionIdx + 2)?.let { reserved++ }

                return@with reserved
            }

            val hasContentsOffset = if (hasContents) 1 else 0
            val containerMaxLength = maxLength - reserved - hasContentsOffset

            if (newIdx > containerMaxLength) {
                val name = "jbp.f${functionId++}"
                val func = CodeFunction(name)

                val splitEnd = container.actions.size

                val subList = container.actions.subList(actionIdx, splitEnd).apply {
                    add(callFunctionAction(name))
                }

                // Concurrent Modification Exception
                container.actions.removeAll(subList)
                func.actions.addAll(subList)

                // Concurrent Modification Exception
                addHandler(func)
                idx++
                break
            }

            idx = newIdx
            if (isContainer) {
                walk(action as CodeContainingAction, maxLength - reserved)
            }
        }
    }

    private class BuilderScope(
        var idx: Int,
        var functionId: Int,
        private val result: MutableList<CodeHandler>
    ) {

        fun addHandler(codeHandler: CodeHandler) {
            result += codeHandler
        }

    }

    private fun callFunctionAction(functionName: String): CodeAction {
        return CodeBasicAction(
            id = Actions.CALL_FUNCTION,
            args = mapOf("function_name" to TextConstant(functionName)),
            conditional = null
        )
    }

    fun toJson(): JsonObject {
        build()

        return buildJsonObject {
            put("handlers", values.map(CodeHandler::toJson))
        }
    }

}