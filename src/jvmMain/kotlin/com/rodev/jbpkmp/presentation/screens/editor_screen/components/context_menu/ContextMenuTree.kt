package com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class ContextTreeNode {
    abstract val name: String

    internal var mutableVisibility by mutableStateOf(true)
    val visible: Boolean
        get() = mutableVisibility

    class Root(
        val child: List<ContextTreeNode> = emptyList(),
        override val name: String
    ) : ContextTreeNode()

    class Leaf(
        override val name: String,
        val id: String
    ) : ContextTreeNode()
}

fun ContextTreeNode.updateVisibility(predicate: (ContextTreeNode.Leaf) -> Boolean): Boolean {
    when (this) {
        is ContextTreeNode.Leaf -> {
            val result = predicate(this)
            mutableVisibility = result
            return result
        }
        is ContextTreeNode.Root -> {
            var result = false
            child.forEach {
                val iterationResult = it.updateVisibility(predicate)

                if (iterationResult) {
                    result = true
                }
            }

            mutableVisibility = result

            return result
        }
    }
}

class TreeNodeBuilder private constructor() : RootBuilderScope {

    private val treeNodes = mutableListOf<ContextTreeNode>()

    override fun root(name: String, content: RootBuilderScope.() -> Unit) {
        val treeNodeBuilder = TreeNodeBuilder()
        content(treeNodeBuilder)
        treeNodes.add(ContextTreeNode.Root(treeNodeBuilder.build(), name))
    }

    override fun leaf(name: String, id: String) {
        treeNodes.add(ContextTreeNode.Leaf(name, id))
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

    fun leaf(name: String, id: String)
}