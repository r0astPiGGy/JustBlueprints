package com.rodev.jbpkmp.domain.source

import com.rodev.generator.action.entity.NodeModel
import com.rodev.jbpkmp.presentation.screens.editor_screen.toNode
import com.rodev.nodeui.model.Node

interface NodeDataSource {

    fun getNodeModelById(id: String): NodeModel?

}

fun NodeDataSource.getNodeById(id: String): Node {
    return getNodeModelById(id)!!.toNode()
}