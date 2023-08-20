package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import com.rodev.generator.action.entity.ActionType
import com.rodev.jbpkmp.domain.source.ActionDataSource
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.model.Node

class NodeStateFactoryRegistry(
    private val actionDataSource: ActionDataSource
) : NodeStateFactory {

    companion object {

        const val NODE_TYPE_TAG = "type"

    }

    private val nodeFactoriesByNodeId = hashMapOf<String, NodeStateFactory>()
    private val nodeFactoriesByNodeType = hashMapOf<ActionType, NodeStateFactory>()
    private var defaultNodeStateFactory: NodeStateFactory? = null

    fun registerNodeStateFactoryByNodeId(nodeId: String, nodeStateFactory: NodeStateFactory) {
        nodeFactoriesByNodeId[nodeId] = nodeStateFactory
    }

    fun registerNodeStateFactoryByActionType(actionType: ActionType, nodeStateFactory: NodeStateFactory) {
        nodeFactoriesByNodeType[actionType] = nodeStateFactory
    }

    fun setDefaultNodeStateFactory(nodeStateFactory: NodeStateFactory) {
        this.defaultNodeStateFactory = nodeStateFactory
    }

    override fun createNodeState(node: Node): NodeState {
        val type = node.tag.getString(NODE_TYPE_TAG)

        require(type != null) { "Invalid node provided: No type tag" }

        val nodeStateFactory = nodeFactoriesByNodeId[type]

        if (nodeStateFactory != null) {
            return nodeStateFactory.createNodeState(node)
        }

        val action = actionDataSource.getActionById(type)

        if (action != null) {
            val nodeState = createNodeStateByActionType(node, action.type)

            if (nodeState != null) {
                return nodeState
            }
        }

        val defaultNodeStateFactory = defaultNodeStateFactory

        require(defaultNodeStateFactory != null) { "Default NodeStateFactory is not registered!" }

        return defaultNodeStateFactory.createNodeState(node)
    }

    private fun createNodeStateByActionType(node: Node, actionType: ActionType): NodeState? {
        return nodeFactoriesByNodeType[actionType]?.createNodeState(node)
    }

}

