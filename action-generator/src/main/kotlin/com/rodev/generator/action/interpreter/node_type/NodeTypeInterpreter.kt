package com.rodev.generator.action.interpreter.node_type

import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.NodeType

class NodeTypeInterpreter(
    private val nodes: List<NodeModel>
) {

    fun interpret(): List<NodeType> {
        return nodes.map { it.type }.distinct().map { NodeType(it, color = 0) }
    }

}