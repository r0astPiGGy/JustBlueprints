package com.rodev.jbpkmp.presentation.screens.editor_screen

import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.PinModel
import com.rodev.nodeui.model.Node
import com.rodev.nodeui.model.Pin
import com.rodev.nodeui.model.tag.MapTag
import com.rodev.nodeui.model.tag.MapTagBuilderScope
import com.rodev.nodeui.model.tag.buildMapTag
import com.rodev.nodeui.model.tag.inheritBuilder
import java.util.UUID

const val VARIABLE_TYPE_TAG = "variable"
const val NODE_TYPE_ID_TAG = "type"
const val PIN_TYPE_ID_TAG = "type"
const val PIN_VALUE_TAG = "value"
const val VARIABLE_ID_TAG = "variableId"
const val FUNCTION_ID_TAG = "functionId"
const val INVOKABLE_DECLARATION_ID = "invokableId"
const val PROCESS_ID_TAG = "processId"

fun VariableState.toNode(): Node {
    return getVariableNode(this)
}

private fun getVariableNode(variable: VariableState): Node {
    return Node(
        x = 0f,
        y = 0f,
        uniqueId = UUID.randomUUID().toString(),
        inputPins = emptyList(),
        outputPins = listOf(createPin(typeId = VARIABLE_TYPE_TAG)),
        tag = createVariableNodeTag(variableId = variable.id)
    )
}

fun createVariableNodeTag(variableId: String): MapTag {
    return createNodeTypeTag(VARIABLE_TYPE_TAG) {
        putString(VARIABLE_ID_TAG, variableId)
    }
}

fun Pin.getId(): String = getString(PIN_TYPE_ID_TAG)

fun Pin.getValue(): String? = tag.getString(PIN_VALUE_TAG)

fun Pin.getString(id: String): String {
    return tag.getStringNotNull(id)
}

fun Node.getType() = getString(NODE_TYPE_ID_TAG)

fun Node.getInvokableId() = getString(INVOKABLE_DECLARATION_ID)

fun MapTagBuilderScope.setInvokableId(id: String) {
    putString(INVOKABLE_DECLARATION_ID, id)
}

fun Node.setInvokableId(id: String): Node {
    return copy(
        tag = tag.inheritBuilder {
            this@inheritBuilder.setInvokableId(id)
        }
    )
}

fun Node.getString(id: String): String {
    return tag.getStringNotNull(id)
}

fun MapTagBuilderScope.setFunctionId(id: String) {
    putString(FUNCTION_ID_TAG, id)
}

fun MapTagBuilderScope.setProcessId(id: String) {
    putString(PROCESS_ID_TAG, id)
}

private fun MapTag.getStringNotNull(id: String): String {
    return requireNotNull(getString(id)) { "Tag by id '$id' not found" }
}

fun createNodeTypeTag(
    typeId: String,
    continueFunction: MapTagBuilderScope.() -> Unit = {}
): MapTag {
    return buildMapTag {
        putString(NODE_TYPE_ID_TAG, typeId)
        continueFunction()
    }
}

fun createPinTag(
    typeId: String,
    value: String?,
    continueFunction: MapTagBuilderScope.() -> Unit = {}
): MapTag {
    return buildMapTag {
        putString(PIN_TYPE_ID_TAG, typeId)
        value?.let { putString(PIN_VALUE_TAG, it) }
        continueFunction()
    }
}

fun MapTag.inheritBuilder(function: MapTagBuilderScope.() -> Unit): MapTag {
    return inheritBuilder(this, function)
}

fun createPin(
    id: String = UUID.randomUUID().toString(),
    typeId: String,
    value: String? = null
): Pin = Pin(
    uniqueId = id,
    tag = createPinTag(
        typeId = typeId,
        value = value
    )
)

fun NodeModel.toNode(): Node {
    return Node(
        x = 0f,
        y = 0f,
        uniqueId = UUID.randomUUID().toString(),
        inputPins = input.map { it.toPin() },
        outputPins = output.map { it.toPin() },
        createNodeTypeTag(id)
    )
}

fun PinModel.toPin(): Pin {
    return createPin(typeId = id)
}