package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.model.Node

class NodeStateFactoryRegistry : NodeStateFactory {

    companion object {

        const val NODE_TYPE_TAG = "type"

    }

    private val registeredNodeFactories = hashMapOf<String, NodeStateFactory>()
    private var defaultNodeStateFactory: NodeStateFactory? = null

    fun registerNodeStateFactory(typeId: String, nodeStateFactory: NodeStateFactory) {
        registeredNodeFactories[typeId] = nodeStateFactory
    }

    fun setDefaultNodeStateFactory(nodeStateFactory: NodeStateFactory) {
        this.defaultNodeStateFactory = nodeStateFactory
    }

    override fun createNodeState(node: Node): NodeState {
        val type = node.tag.getString(NODE_TYPE_TAG)

        require(type != null) { "Invalid node provided: No type tag" }

        val nodeStateFactory = registeredNodeFactories.getOrDefault(type, defaultNodeStateFactory)

        require(nodeStateFactory != null) { "NodeStateFactory is not registered: Unknown node type '$type'" }

        return nodeStateFactory.createNodeState(node)
    }

}

