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

typealias ContextMenuItemProvider = () -> List<ContextTreeNode>

typealias OnTreeNodeClick = (ContextTreeNode.Leaf) -> Unit

@Composable
fun BlueprintContextMenu(
    headerText: String,
    onDismiss: () -> Unit,
    onTreeNodeClick: OnTreeNodeClick,
    treeNodes: List<ContextTreeNode>
) {
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
                text = headerText,
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
                // Updates visibility of tree nodes when input changes
                LaunchedEffect(queryInput) {
                    val predicate: TreeQuery = { name ->
                        name.lowercase().contains(queryInput.lowercase())
                    }

                    treeNodes.forEach { treeNode ->
                        treeNode.updateVisibility {
                            predicate(it.name)
                        }
                    }
                }

                val scrollState = rememberLazyListState()

                CompositionLocalProvider(
                    LocalOnTreeNodeClick provides onTreeNodeClick
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 12.dp),
                        state = scrollState
                    ) {
                        items(treeNodes) {
                            TreeNode(it)
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = scrollState
                    )
                )
            }
        }
    }
}

typealias TreeQuery = (String) -> Boolean

private val LocalOnTreeNodeClick = compositionLocalOf<OnTreeNodeClick> { { } }

@Composable
fun TreeNode(
    node: ContextTreeNode
) {
    when (node) {
        is ContextTreeNode.Leaf -> {
            TreeNodeLeaf(
                nodeLeaf = node
            )
        }

        is ContextTreeNode.Root -> {
            TreeNodeRoot(
                nodeRoot = node
            )
        }

        else -> {}
    }
}

@Composable
fun TreeNodeLeaf(
    nodeLeaf: ContextTreeNode.Leaf
) {
    if (nodeLeaf.visible) {
        val onTreeNodeClick = LocalOnTreeNodeClick.current

        Text(
            text = nodeLeaf.name,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onTreeNodeClick(nodeLeaf)
                }
        )
    }
}

@Composable
fun TreeNodeRoot(
    nodeRoot: ContextTreeNode.Root
) {
    if (!nodeRoot.visible) return

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
            CategoryArrow(
                modifier = Modifier
                    .requiredSize(10.dp),
                expanded = visible
            )
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
                    TreeNode(it)
                }
            }
        }
    }
}

@Composable
private fun CategoryArrow(
    modifier: Modifier,
    expanded: Boolean
) {
    Canvas(
        modifier = modifier
    ) {
        val path = Path()
        val drawStyle: DrawStyle

        if (expanded) {
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
}