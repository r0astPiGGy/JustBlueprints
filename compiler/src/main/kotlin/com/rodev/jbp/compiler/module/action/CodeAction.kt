package com.rodev.jbp.compiler.module.action

import com.rodev.jbp.compiler.json.putNotNull
import com.rodev.jbp.compiler.module.CodeBlock
import com.rodev.jbp.compiler.module.value.Value
import kotlinx.serialization.json.*

typealias CodeActionArguments = Map<String, Value>

abstract class CodeAction(
    val id: String,
    val args: CodeActionArguments = emptyMap(),
    val selection: String? = null,
    val conditional: CodeAction?
) : CodeBlock() {

    override val length: Int
        get() = LENGTH

    fun valuesToJson(): JsonArray {
        return buildJsonArray {
            args.map { (key, value) ->
                addJsonObject {
                    put("name", key)
                    put("value", value.toJson())
                }
            }
        }
    }

    override fun toJson(): JsonObject {
        val selection = selection?.let {
            buildJsonObject {
                put("type", it)
            }
        }
        
        val values = conditional?.valuesToJson() ?: valuesToJson()
        val conditional = this.conditional?.toJson()
        
        return buildJsonObject { 
            put("action", id)
            put("values", values)
            putNotNull("selection", selection)
            putNotNull("conditional", conditional)
        }
    }

    companion object {
        const val LENGTH = 1
    }

}