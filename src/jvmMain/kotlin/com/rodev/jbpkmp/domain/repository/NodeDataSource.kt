package com.rodev.jbpkmp.domain.repository

import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.PinModel
import com.rodev.jbpkmp.presentation.screens.editor_screen.VariableState
import com.rodev.nodeui.model.Node
import com.rodev.nodeui.model.Pin
import java.util.*

interface NodeDataSource {

    fun getNodeModelById(id: String): NodeModel

}

fun NodeDataSource.getNodeById(id: String): Node {
    return getNodeModelById(id).toNode()
}

const val variableTypeId = "variable"

fun getVariableNode(variable: VariableState): Node {
    return Node(
        x = 0f,
        y = 0f,
        uniqueId = UUID.randomUUID().toString(),
        typeId = variableTypeId,
        inputPins = emptyList(),
        outputPins = listOf(
            Pin(
                uniqueId = UUID.randomUUID().toString(),
                typeId = variable.id,
                value = null
            )
        ),
    )
}

fun NodeModel.toNode(): Node {
    return Node(
        x = 0f,
        y = 0f,
        uniqueId = UUID.randomUUID().toString(),
        typeId = id,
        inputPins = input.map { it.toPin() },
        outputPins = output.map { it.toPin() },
    )
}

fun PinModel.toPin(): Pin {
    return Pin(
        uniqueId = UUID.randomUUID().toString(),
        typeId = id,
        value = null
    )
}