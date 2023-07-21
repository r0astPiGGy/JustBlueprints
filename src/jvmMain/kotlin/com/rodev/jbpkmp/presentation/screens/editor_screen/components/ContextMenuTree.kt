package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import com.rodev.nodeui.model.Node

sealed interface ContextTreeNode {
    val name: String

    class Root(
        val child: List<ContextTreeNode> = emptyList(),
        override val name: String
    ) : ContextTreeNode

    class Leaf(
        override val name: String,
        val node: Node
    ) : ContextTreeNode
}

class TreeNodeBuilder private constructor() : RootBuilderScope {

    private val treeNodes = mutableListOf<ContextTreeNode>()

    override fun root(name: String, content: RootBuilderScope.() -> Unit) {
        val treeNodeBuilder = TreeNodeBuilder()
        content(treeNodeBuilder)
        treeNodes.add(ContextTreeNode.Root(treeNodeBuilder.build(), name))
    }

    override fun leaf(name: String, node: Node) {
        treeNodes.add(ContextTreeNode.Leaf(name, node))
    }

    private fun build(): List<ContextTreeNode> {
        return treeNodes
    }

    companion object {

        fun create(content: RootBuilderScope.() -> Unit): List<ContextTreeNode> {
            val builder = TreeNodeBuilder()

            content(builder)

            return builder.build()
        }

    }

}

interface RootBuilderScope {
    fun root(name: String, content: RootBuilderScope.() -> Unit)

    fun leaf(name: String, node: Node)
}