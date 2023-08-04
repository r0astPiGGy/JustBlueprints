package com.rodev.nodeui.components.node

import com.rodev.nodeui.model.Node

interface NodeStateFactory {

    fun createNodeState(node: Node): NodeState

}