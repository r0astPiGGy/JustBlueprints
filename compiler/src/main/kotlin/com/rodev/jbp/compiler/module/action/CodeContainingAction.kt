package com.rodev.jbp.compiler.module.action

import com.rodev.jbp.compiler.json.inheritBuilder
import com.rodev.jbp.compiler.json.put
import com.rodev.jbp.compiler.module.CodeBlock
import com.rodev.jbp.compiler.module.CodeContainer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class CodeContainingAction(
    id: String,
    args: CodeActionArguments = emptyMap(),
    selection: String? = null,
    conditional: CodeAction?
) : CodeAction(id, args, selection, conditional), CodeContainer {

    override val actions = mutableListOf<CodeAction>()
    var isInverted = false

    override val length: Int
        get() = 1 + actions.map(CodeBlock::length).sum()

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("is_inverted", isInverted)
            put("operations", actions.map(CodeBlock::toJson))
        }
    }

    companion object {

        const val LENGTH = 2

    }
}