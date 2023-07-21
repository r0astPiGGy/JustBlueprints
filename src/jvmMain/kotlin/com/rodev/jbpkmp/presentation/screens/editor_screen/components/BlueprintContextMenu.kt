package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodev.jbpkmp.theme.gray

@Composable
fun BlueprintContextMenu(onDismiss: () -> Unit) {
    ContextMenu(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .requiredSize(500.dp)
                .background(color = gray)
                .padding(8.dp)
        ) {
            Text(
                text = "All actions for this Blueprint",
                fontSize = 20.sp
            )

            var queryInput by remember { mutableStateOf("") }
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = queryInput,
                onValueChange = { queryInput = it },
                singleLine = true,
                placeholder = {
                    Text("Search...")
                }
            )
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Gray)
                    .padding(10.dp)
            ) {

                val queryPredicate: TreeQuery = remember { { s ->
                    s.lowercase().contains(queryInput.lowercase()) }
                }
                val state = rememberLazyListState()

                CompositionLocalProvider(LocalTreeQuery provides queryPredicate) {
                    LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state) {
                        items(createSampleTree()) {
                            TreeNode(it)
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
            }
        }
    }
}

typealias TreeQuery = (String) -> Boolean

val LocalTreeQuery = compositionLocalOf<TreeQuery> { { true } }

fun createSampleTree(): List<ContextTreeNode> {
    return TreeNodeBuilder.create {
        root(name = "Функции") {
            root(name = "Разное") {
                leaf("Test4Function")
            }
            leaf("Test1Function")
            leaf("TestFunction")
        }
        root(name = "Ивенты") {
            root(name = "Игрок") {
                leaf("Игрок зашёл")
                leaf("Игрок вышел")
            }
            root(name = "Моб") {
                leaf("Моб умер")
                leaf("Моб заспавнился")
            }
        }
    }
}

class TreeNodeBuilder : RootBuilderScope {

    private val treeNodes = mutableListOf<ContextTreeNode>()

    override fun root(name: String, content: RootBuilderScope.() -> Unit) {
        val treeNodeBuilder = TreeNodeBuilder()
        content(treeNodeBuilder)
        treeNodes.add(ContextTreeNode.Root(treeNodeBuilder.build(), name))
    }

    override fun leaf(name: String) {
        treeNodes.add(ContextTreeNode.Leaf(name))
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

    fun leaf(name: String)
}

typealias NodeParent = ContextTreeNode.Root?

@Composable
fun TreeNode(node: ContextTreeNode, parent: NodeParent = null) {
    when (node) {
        is ContextTreeNode.Leaf -> {
            TreeNodeLeaf(nodeLeaf = node, parent = parent)
        }
        is ContextTreeNode.Root -> {
            TreeNodeRoot(nodeRoot = node, parent = parent)
        }
    }
}

@Composable
fun TreeNodeLeaf(nodeLeaf: ContextTreeNode.Leaf, parent: NodeParent) {
    if (LocalTreeQuery.current(nodeLeaf.name)) {
        Text(
            text = nodeLeaf.name,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun TreeNodeRoot(nodeRoot: ContextTreeNode.Root, parent: NodeParent) {
    Column {
        var visible by remember { mutableStateOf(true) }
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .clickable {
                    visible = !visible
                }
        ) {
            Canvas(
                modifier = Modifier
                    .requiredSize(10.dp)
            ) {
                val path = Path()
                val drawStyle: DrawStyle

                if (visible) {
                    drawStyle = Fill

                    path.moveTo(size.width, 0f)
                    path.lineTo(0f, size.height)
                    path.lineTo(size.width, size.height)
                    path.lineTo(size.width, 0f)
                } else {
                    drawStyle = Stroke(width = 1f)

                    path.moveTo(center.x, 0f)
                    path.lineTo(center.x, size.height)
                    path.lineTo(size.width, center.y)
                    path.lineTo(center.x, 0f)
                }

                drawPath(path, color = Color.White, style = drawStyle)
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(nodeRoot.name)
        }
        if (visible) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp)
            ) {
                nodeRoot.child.forEach {
                    TreeNode(it, parent = nodeRoot)
                }
            }
        }
    }
}

sealed interface ContextTreeNode {
    val name: String

    class Root(
        val child: List<ContextTreeNode> = emptyList(),
        override val name: String
    ) : ContextTreeNode

    class Leaf(
        override val name: String
    ) : ContextTreeNode
}