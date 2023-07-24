package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

typealias ContextMenuItemProvider = () -> List<ContextTreeNode>

typealias OnTreeNodeClick = (ContextTreeNode.Leaf) -> Unit

@Composable
fun BlueprintContextMenu(
    borderColor: Color,
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
                .border(1.dp, borderColor, RoundedCornerShape(10.dp))
        ) {
            var queryInput by remember { mutableStateOf("") }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .clip(
                        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    ),
                value = queryInput,
                onValueChange = { queryInput = it },
                singleLine = true,
                placeholder = { Text("Search...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Box(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colors.surface)
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
    }
}

@Composable
fun TreeNodeLeaf(
    nodeLeaf: ContextTreeNode.Leaf
) {
    if (nodeLeaf.visible) {
        val onTreeNodeClick = LocalOnTreeNodeClick.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .clickable { onTreeNodeClick(nodeLeaf) }
        ) {
            Text(
                text = nodeLeaf.name,
                modifier = Modifier.padding(2.dp),
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
fun TreeNodeRoot(
    nodeRoot: ContextTreeNode.Root
) {
    if (!nodeRoot.visible) return

    var visible by remember { mutableStateOf(true) }
    val rotation by animateFloatAsState(
        targetValue = if (visible) 0F else -90F
    )

    Column {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .clickable { visible = !visible }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.rotate(rotation)
            )

            Text(nodeRoot.name)
        }

        AnimatedVisibility(visible) {
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