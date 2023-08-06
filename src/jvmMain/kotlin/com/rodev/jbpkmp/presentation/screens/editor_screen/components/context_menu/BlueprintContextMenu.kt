package com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

typealias ContextMenuItemProvider = () -> List<ContextTreeNode>

typealias ContextMenuModelProvider = () -> ContextMenuModel

typealias OnTreeNodeClick = (ContextTreeNode.Leaf) -> Unit

data class ContextMenuModel(
    val borderColor: Color,
    val contextMenuItemProvider: ContextMenuItemProvider
)

@Composable
fun BlueprintContextMenu(
    contextMenuModelProvider: ContextMenuModelProvider,
    onDismiss: () -> Unit,
    onTreeNodeClick: OnTreeNodeClick,
    expanded: Boolean
) {
    ContextMenu(
        onDismissRequest = onDismiss,
        expanded = expanded
    ) {
        val contextMenuModel = remember { contextMenuModelProvider() }
        val treeNodes = remember { contextMenuModel.contextMenuItemProvider() }
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, contextMenuModel.borderColor)
        ) {
            Column(
                modifier = Modifier
                    .requiredSize(500.dp)
            ) {
                var queryInput by remember { mutableStateOf("") }

                TextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
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

                    val scrollState = rememberScrollState()

                    CompositionLocalProvider(
                        LocalOnTreeNodeClick provides onTreeNodeClick
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 12.dp)
                                .verticalScroll(state = scrollState),
                        ) {
                            treeNodes.forEach {
                                TreeNode(it)
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .clickable { onTreeNodeClick(nodeLeaf) }
        ) {
            Spacer(
                modifier = Modifier.width(5.dp)
            )
            Image(
                painter = nodeLeaf.icon,
                modifier = Modifier
                    .size(25.dp),
                contentDescription = null
            )
            Text(
                text = nodeLeaf.name,
                modifier = Modifier.padding(2.dp),
                color = MaterialTheme.colors.onSurface
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Composable
fun TreeNodeRoot(
    nodeRoot: ContextTreeNode.Root
) {
    if (!nodeRoot.visible) return

    var visible by remember { mutableStateOf(false) }
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
    Spacer(modifier = Modifier.height(5.dp))
}