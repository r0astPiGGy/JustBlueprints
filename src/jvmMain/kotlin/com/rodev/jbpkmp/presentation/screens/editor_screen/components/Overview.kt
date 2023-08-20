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
import com.rodev.jbpkmp.presentation.localization.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.*
import org.koin.compose.koinInject

@Composable
fun OverviewPanel(
    modifier: Modifier = Modifier,
    viewModel: EditorScreenViewModel
) {
    val selectionHandler = koinInject<SelectionHandler>()

    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colors.background)
    ) {
        item {
            LocalVariables(viewModel, selectionHandler)
        }
        item {
            GlobalVariables(viewModel, selectionHandler)
        }
        item {
            Functions(viewModel, selectionHandler)
        }
        item {
            Processes(viewModel, selectionHandler)
        }
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
        currentGraph?.variables?.forEach {
            VariableView(selectionHandler, it)
        }
    }

    if (createVariableDialogPresented) {
        CreateVariableDialog(
            onDismissRequest = { createVariableDialogPresented = false },
            onSelect = {
                EditorScreenEvent.AddLocalVariable(
                    name = it
                ).let(viewModel::onEvent)
            }
        )
    }
}

@Composable
private fun Functions(
    viewModel: EditorScreenViewModel,
    selectionHandler: SelectionHandler
) {
    var createDialogPresented by remember { mutableStateOf(false) }

    CollapsibleList(
        header = Vocabulary.localization.functions(),
        onAddAction = {
            createDialogPresented = true
        }
    ) {
        viewModel.functions.forEach {
            InvokableView(selectionHandler, it) { _ ->
                viewModel.onEvent(EditorScreenEvent.OpenFunction(it))
            }
        }
    }

    if (createDialogPresented) {
        CreateVariableDialog(
            onDismissRequest = { createDialogPresented = false },
            onSelect = {
                EditorScreenEvent.AddFunction(
                    name = it
                ).let(viewModel::onEvent)
            }
        )
    }
}

@Composable
private fun Processes(
    viewModel: EditorScreenViewModel,
    selectionHandler: SelectionHandler
) {
    var createDialogPresented by remember { mutableStateOf(false) }

    CollapsibleList(
        header = Vocabulary.localization.processes(),
        onAddAction = {
            createDialogPresented = true
        }
    ) {
        viewModel.processes.forEach {
            InvokableView(selectionHandler, it) { _ ->
                viewModel.onEvent(EditorScreenEvent.OpenProcess(it))
            }
        }
    }

    if (createDialogPresented) {
        CreateVariableDialog(
            onDismissRequest = { createDialogPresented = false },
            onSelect = {
                EditorScreenEvent.AddProcess(
                    name = it
                ).let(viewModel::onEvent)
            }
        )
    }
}

@Composable
fun InvokableView(
    selectionHandler: SelectionHandler,
    invokable: InvokableState,
    onSelectedClick: (InvokableState) -> Unit
) {
    Surface(
        border = if (invokable.selected) BorderStroke(2.dp, Color.White) else null,
        shape = RoundedCornerShape(size = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable {
                    if (invokable.selected) {
                        onSelectedClick(invokable)
                    } else {
                        selectionHandler.onSelect(
                            invokable
                        )
                    }
                }
                .fillMaxWidth()
                .padding(6.dp),
        ) {
            DragTarget(
                dataToDrop = invokable,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    invokable.name
                )
            }
        }
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
                    if (!variable.selected) {
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
        viewModel.globalVariables.forEach {
            VariableView(selectionHandler, it)
        }
    }

    if (createVariableDialogPresented) {
        CreateVariableDialog(
            onDismissRequest = { createVariableDialogPresented = false },
            onSelect = {
                EditorScreenEvent.AddGlobalVariable(
                    name = it,
                    type = Variable.Type.Game
                ).let(viewModel::onEvent)
            }
        )
    }
}

@Composable
fun CollapsibleList(
    header: String,
    onAddAction: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
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
            Column(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                content = content
            )
        }
    }
}
