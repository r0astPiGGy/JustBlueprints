package com.rodev.jbpkmp.domain.repository

import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.PinModel
import com.rodev.jbpkmp.presentation.screens.editor_screen.VariableState
import com.rodev.jbpkmp.presentation.screens.editor_screen.toNode
import com.rodev.nodeui.model.Node
import com.rodev.nodeui.model.Pin
import com.rodev.nodeui.model.tag.MapTag
import com.rodev.nodeui.model.tag.MapTagBuilderScope
import com.rodev.nodeui.model.tag.buildMapTag
import java.util.*

interface NodeDataSource {

    fun getNodeModelById(id: String): NodeModel

}

fun NodeDataSource.getNodeById(id: String): Node {
    return getNodeModelById(id).toNode()
}