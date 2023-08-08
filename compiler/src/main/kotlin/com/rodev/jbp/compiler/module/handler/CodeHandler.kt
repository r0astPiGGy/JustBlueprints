package com.rodev.jbp.compiler.module.handler

import com.rodev.jbp.compiler.json.put
import com.rodev.jbp.compiler.module.action.CodeAction
import com.rodev.jbp.compiler.module.CodeBlock
import com.rodev.jbp.compiler.module.CodeContainer
import kotlinx.serialization.json.*

abstract class CodeHandler(
    val type: String
) : CodeBlock(), CodeContainer {

    override val actions = mutableListOf<CodeAction>()
    var position: Int = 0

    override val length: Int
        get() = actions.map(CodeBlock::length).sum()

    override fun toJson(): JsonObject {
        return buildJsonObject {
            put("type", type)
            put("position", position)
            put("operations", actions.map(CodeBlock::toJson))
        }
    }

    companion object {

        const val MAX_LENGTH = 43

    }
}