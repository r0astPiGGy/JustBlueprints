package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.domain.model.variable.Variable
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.globalVariables
import com.rodev.jbpkmp.presentation.localization.localVariables
import com.rodev.jbpkmp.presentation.screens.editor_screen.*
import org.koin.compose.koinInject

@Composable
fun OverviewPanel(
    modifier: Modifier = Modifier,
    viewModel: EditorScreenViewModel
) {
    val selectionHandler = koinInject<SelectionHandler>()

    Column(
        modifier = modifier
            .background(MaterialTheme.colors.background)
    ) {
        LocalVariables(viewModel, selectionHandler)
        GlobalVariables(viewModel, selectionHandler)
    }
}

@Composable
private fun LocalVariables(
    viewModel: EditorScreenViewModel,
    selectionHandler: SelectionHandler
) {
    val localization = Vocabulary.localization
    val currentGraph = viewModel.currentGraph
    var createVariableDialogPresented by remember { mutableStateOf(false) }

    CollapsibleList(
        header = localization.localVariables(),
        onAddAction = {
            createVariableDialogPresented = true
        }
    ) {
        if (currentGraph != null) {
            items(currentGraph.variables, key = { it.id }) {
                VariableView(selectionHandler, it)
            }
        }
    }

    if (createVariableDialogPresented) {
        CreateVariableDialog(
            onDismissRequest = { createVariableDialogPresented = false },
            onSelect = {
                EditorScreenEvent.AddLocalVariable(
                    LocalVariableState(
                        name = it
                    )
                ).let(viewModel::onEvent)
            }
        )
    }
}

@Composable
fun VariableView(
    selectionHandler: SelectionHandler,
    variable: VariableState
) {
    Surface(
        border = if (variable.selected) BorderStroke(2.dp, Color.White) else null,
        shape = RoundedCornerShape(size = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable {
                    if (variable.selected) {
                        selectionHandler.resetSelection()
                    } else {
                        selectionHandler.onSelect(
                            variable
                        )
                    }
                }
                .fillMaxWidth()
                .padding(6.dp),
        ) {
            DragTarget(
                dataToDrop = variable,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    variable.name
                )
            }
        }
    }
}

@Composable
private fun GlobalVariables(
    viewModel: EditorScreenViewModel,
    selectionHandler: SelectionHandler
) {
    val localization = Vocabulary.localization
    var createVariableDialogPresented by remember { mutableStateOf(false) }

    CollapsibleList(
        header = localization.globalVariables(),
        onAddAction = {
            createVariableDialogPresented = true
        }
    ) {
        items(viewModel.state.variables, key = { it.id }) {
            VariableView(selectionHandler, it)
        }
    }

    if (createVariableDialogPresented) {
        CreateVariableDialog(
            onDismissRequest = { createVariableDialogPresented = false },
            onSelect = {
                EditorScreenEvent.AddGlobalVariable(
                    GlobalVariableState(
                        name = it,
                        // TODO
                        type = Variable.Type.Game
                    )
                ).let(viewModel::onEvent)
            }
        )
    }
}

@Composable
fun CollapsibleList(
    header: String,
    onAddAction: () -> Unit = {},
    content: LazyListScope.() -> Unit
) {
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
                modifier = Modifier.rotate(rotation),
                tint = MaterialTheme.colors.onBackground
            )

            Text(
                text = header,
                color = MaterialTheme.colors.onBackground
            )

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onAddAction) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }
        }

        AnimatedVisibility(visible) {
            LazyColumn(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                content = content
            )
        }
    }
}
