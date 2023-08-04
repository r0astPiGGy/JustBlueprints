package com.rodev.nodeui.components.node

import androidx.compose.runtime.Composable
import com.rodev.nodeui.model.Node

interface NodeDisplay {

    @Composable
    fun NodeView(nodeState: NodeState)

    fun toNode(nodeState: NodeState): Node

}