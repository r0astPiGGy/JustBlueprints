package com.rodev.jbpkmp.domain.compiler

import com.rodev.jbp.compiler.module.action.CodeAction
import com.rodev.jbp.compiler.module.action.CodeBasicAction
import com.rodev.jbp.compiler.module.value.Value
import com.rodev.jbp.compiler.module.value.ValueType
import com.rodev.jbp.compiler.module.value.constants.TextConstant

object Nodes {

    object Type {

        const val BRANCH = "native_branch"
        const val FUNCTION_DECLARATION = "native_function_declaration"
        const val PROCESS_DECLARATION = "native_process_declaration"
        const val FUNCTION_REFERENCE = "native_function_reference"
        const val PROCESS_REFERENCE = "native_process_reference"
    }
    
    enum class Factory(val id: String, val valueType: ValueType) {
        ITEM("native_item_factory", ValueType.Item),
        LOCATION("native_location_factory", ValueType.Location),
        TEXT("native_text_factory", ValueType.Text),
        NUMBER("native_number_factory", ValueType.Number),
        SOUND("native_sound_factory", ValueType.Sound),
        ARRAY("native_array_factory", ValueType.Array)

        ;

        companion object {

            fun ValueType.toFactory(): Factory? {
                return values().find { it.valueType == this }
            }

        }
    }

    object Action {

        val CALL_FUNCTION: (functionName: String) -> CodeAction = { name ->
            CodeBasicAction(
                id = "call_function",
                args = mutableMapOf<String, Value>("function_name" to TextConstant(name)),
                selection = null,
                conditional = null
            )
        }

        val START_PROCESS: (processName: String, args: Map<String, Value>) -> CodeAction = { name, args ->
            CodeBasicAction(
                id = "start_process",
                args = mutableMapOf<String, Value>("process_name" to TextConstant(name)).apply {
                    putAll(args)
                },
                selection = null,
                conditional = null
            )
        }

    }
}