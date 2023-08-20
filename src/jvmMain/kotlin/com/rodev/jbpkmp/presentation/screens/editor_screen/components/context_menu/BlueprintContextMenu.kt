package com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
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
        val focusRequester = remember { FocusRequester() }
        val contextMenuState = remember {
            ContextMenuState(contextMenuModel.contextMenuItemProvider())
        }

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
                        contextMenuState.onInputChange(queryInput)
                    }

                    val scrollState = rememberLazyListState()

                    CompositionLocalProvider(
                        LocalOnTreeNodeClick provides onTreeNodeClick
                    ) {
                        LazyColumn(
                            state = scrollState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 12.dp),
                        ) {
                            items(
                                contextMenuState.nodes,
                                key = ContextTreeNode::name
                            ) {
                                TreeNode(it)
                            }
                        }
                    }

                    // TODO FIX неправильно отображается в связке с LazyColumn
//                    VerticalScrollbar(
//                        modifier = Modifier
//                            .align(Alignment.CenterEnd)
//                            .fillMaxHeight(),
//                        adapter = rememberScrollbarAdapter(
//                            scrollState = scrollState
//                        )
//                    )
                }
            }
        }
    }
}

private class ContextMenuState(
    val nodes: List<ContextTreeNode>
) {

    private var firstExpand = true

    fun onInputChange(input: String) {
        val predicate: TreeQuery = { name ->
            name.lowercase().contains(input.lowercase())
        }

        nodes.forEach { treeNode ->
            treeNode.updateVisibility(
                onEachRoot = {
                    if (it && !firstExpand) {
                        this.expanded = true
                    }
                }
            ) {
                predicate(it.name)
            }
        }

        if (firstExpand) {
            firstExpand = false
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

typealias Content = @Composable () -> Unit

@Composable
fun wrapIf(condition: Boolean, wrapper: @Composable (Content) -> Unit, content: Content) {
    if (condition) {
        wrapper(content)
    } else {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TreeNodeLeaf(
    nodeLeaf: ContextTreeNode.Leaf
) {
    if (nodeLeaf.visible) {
        val onTreeNodeClick = LocalOnTreeNodeClick.current

        wrapIf(
            nodeLeaf.tooltipComposable != null,
            wrapper = {
                TooltipArea(
                    tooltip = {
                        Surface(
                            modifier = Modifier.shadow(4.dp),
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                nodeLeaf.tooltipComposable!!.invoke()
                            }
                        }
                    },
                    content = it
                )
            }
        ) {
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
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Composable
fun TreeNodeRoot(
    nodeRoot: ContextTreeNode.Root
) {
    if (!nodeRoot.visible) return

    val rotation by animateFloatAsState(
        targetValue = if (nodeRoot.expanded) 0F else -90F
    )

    Column {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .clickable { nodeRoot.expanded = !nodeRoot.expanded }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.rotate(rotation)
            )

            Text(nodeRoot.name)
        }

        AnimatedVisibility(nodeRoot.expanded) {
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