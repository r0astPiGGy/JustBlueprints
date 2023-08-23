package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
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
import com.rodev.jbpkmp.presentation.localization.Vocabulary.localization
import com.rodev.jbpkmp.presentation.screens.editor_screen.*
import org.koin.compose.koinInject

class OverviewPanelState {

    var dialogType: DialogType by mutableStateOf(DialogType.Empty)
        private set

    fun onEvent(event: Event) {
        dialogType = when (event) {
            Event.CreateFunction -> DialogType.CreateFunction
            Event.CreateGlobalVariable -> DialogType.CreateGlobalVariable
            Event.CreateLocalVariable -> DialogType.CreateLocalVariable
            Event.CreateProcess -> DialogType.CreateProcess
            Event.CloseDialog -> DialogType.Empty
            is Event.RenameFunction -> DialogType.RenameFunction(event.function)
            is Event.RenameProcess -> DialogType.RenameProcess(event.process)
        }
    }

    sealed class DialogType {
        object CreateLocalVariable : DialogType()
        object CreateGlobalVariable : DialogType()
        object CreateFunction : DialogType()
        object CreateProcess : DialogType()
        data class RenameProcess(val process: ProcessState) : DialogType()
        data class RenameFunction(val function: FunctionState) : DialogType()
        object Empty : DialogType()
    }

    sealed class Event {
        object CreateLocalVariable : Event()
        object CreateGlobalVariable : Event()
        object CreateFunction : Event()
        object CreateProcess : Event()
        object CloseDialog : Event()
        data class RenameProcess(val process: ProcessState) : Event()
        data class RenameFunction(val function: FunctionState) : Event()
    }

}

@Composable
fun OverviewPanel(
    modifier: Modifier = Modifier,
    panelState: OverviewPanelState,
    viewModel: EditorScreenViewModel
) {
    val selectionHandler = koinInject<SelectionHandler>()
    val actionHandler = viewModel.contextMenuActionHandler

    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colors.background)
    ) {
        item("local_variables") {
            LocalVariables(viewModel, panelState, actionHandler, selectionHandler)
        }
        item("global_variables") {
            GlobalVariables(viewModel, panelState, actionHandler, selectionHandler)
        }
        item("functions") {
            Functions(viewModel, panelState, actionHandler, selectionHandler)
        }
        item("processes") {
            Processes(viewModel, panelState, actionHandler, selectionHandler)
        }
    }
}

@Composable
private fun LocalVariables(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState,
    actionHandler: ContextMenuActionHandler,
    selectionHandler: SelectionHandler
) {
    val localization = Vocabulary.localization
    val currentGraph = viewModel.currentGraph

    CollapsibleList(
        header = localization.localVariables(),
        onAddAction = {
            panelState.onEvent(OverviewPanelState.Event.CreateLocalVariable)
        }
    ) {
        currentGraph?.variables?.forEach {
            VariableView(
                selectionHandler,
                it,
                onDelete = {
                    actionHandler.onEvent(ContextMenuEvent.DeleteVariable(it))
                },
                onCopy = {
                    actionHandler.onEvent(ContextMenuEvent.CopyVariable(it))
                },
                onPaste = {
                    actionHandler.onEvent(ContextMenuEvent.PasteVariable(it))
                }
            )
        }
    }
}

@Composable
private fun Functions(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState,
    actionHandler: ContextMenuActionHandler,
    selectionHandler: SelectionHandler
) {
    CollapsibleList(
        header = Vocabulary.localization.functions(),
        onAddAction = {
            panelState.onEvent(OverviewPanelState.Event.CreateFunction)
        }
    ) {
        viewModel.functions.forEach {
            InvokableView(
                selectionHandler = selectionHandler,
                invokable = it,
                onRename = {
                    actionHandler.onEvent(ContextMenuEvent.RenameFunction(it))
                },
                onDelete = {
                    actionHandler.onEvent(ContextMenuEvent.DeleteFunction(it))
                },
                onSelectedClick = {
                    viewModel.onEvent(EditorScreenEvent.OpenFunction(it))
                }
            )
        }
    }
}

@Composable
private fun Processes(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState,
    actionHandler: ContextMenuActionHandler,
    selectionHandler: SelectionHandler
) {
    CollapsibleList(
        header = Vocabulary.localization.processes(),
        onAddAction = {
            panelState.onEvent(OverviewPanelState.Event.CreateProcess)
        }
    ) {
        viewModel.processes.forEach {
            InvokableView(
                selectionHandler = selectionHandler,
                invokable = it,
                onRename = {
                    actionHandler.onEvent(ContextMenuEvent.RenameProcess(it))
                },
                onDelete = {
                    actionHandler.onEvent(ContextMenuEvent.DeleteProcess(it))
                },
                onSelectedClick = {
                    viewModel.onEvent(EditorScreenEvent.OpenProcess(it))
                }
            )
        }
    }
}

@Composable
fun InvokableView(
    selectionHandler: SelectionHandler,
    invokable: InvokableState,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onSelectedClick: () -> Unit
) {

    val localization = Vocabulary.localization

    Surface(
        border = if (invokable.selected) BorderStroke(2.dp, Color.White) else null,
        shape = RoundedCornerShape(size = 6.dp)
    ) {
        ContextMenuArea(
            items = {
                listOf(
                    ContextMenuItem(renameAction(localization), onRename),
                    ContextMenuItem(deleteAction(localization), onDelete)
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .clickable {
                        if (invokable.selected) {
                            onSelectedClick()
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
}

@Composable
fun VariableView(
    selectionHandler: SelectionHandler,
    variable: VariableState,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onDelete: () -> Unit,
) {

    val localization = Vocabulary.localization

    Surface(
        border = if (variable.selected) BorderStroke(2.dp, Color.White) else null,
        shape = RoundedCornerShape(size = 6.dp)
    ) {
        ContextMenuArea(
            items = {
                listOf(
                    ContextMenuItem(pasteAction(localization), onPaste),
                    ContextMenuItem(copyAction(localization), onCopy),
                    ContextMenuItem(deleteAction(localization), onDelete)
                )
            },
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
}

@Composable
private fun GlobalVariables(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState,
    actionHandler: ContextMenuActionHandler,
    selectionHandler: SelectionHandler
) {
    val localization = Vocabulary.localization

    CollapsibleList(
        header = localization.globalVariables(),
        onAddAction = {
            panelState.onEvent(OverviewPanelState.Event.CreateGlobalVariable)
        }
    ) {
        viewModel.globalVariables.forEach {
            VariableView(
                selectionHandler,
                it,
                onDelete = {
                    actionHandler.onEvent(ContextMenuEvent.DeleteVariable(it))
                },
                onCopy = {
                    actionHandler.onEvent(ContextMenuEvent.CopyVariable(it))
                },
                onPaste = {
                    actionHandler.onEvent(ContextMenuEvent.PasteVariable(it))
                }
            )
        }
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
