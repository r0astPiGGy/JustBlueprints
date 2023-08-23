package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.rodev.jbpkmp.domain.model.variable.Variable
import com.rodev.jbpkmp.presentation.components.Sheet
import com.rodev.jbpkmp.presentation.components.validator.textValidator
import com.rodev.jbpkmp.presentation.localization.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.EditorScreenEvent
import com.rodev.jbpkmp.presentation.screens.editor_screen.EditorScreenViewModel

@Composable
fun EditorScreenDialogs(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState
) {
    CreateDialogs(viewModel, panelState)
    RenameDialogs(viewModel, panelState)
}

@Composable
private fun CreateDialogs(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState
) {
    CreateFunctionDialog(viewModel, panelState)
    CreateProcessDialog(viewModel, panelState)
    CreateGlobalVariableDialog(viewModel, panelState)
    CreateLocalVariableDialog(viewModel, panelState)
}

@Composable
private fun RenameDialogs(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState
) {
    RenameFunctionDialog(viewModel, panelState)
    RenameProcessDialog(viewModel, panelState)
}

@Composable
fun CreateProcessDialog(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState
) {
    val localization = Vocabulary.localization

    CreateInvokableDialog(
        inputName = localization.dialogProcessName(),
        validator = remember(localization) {
            textValidator(error = { localization.processAlreadyExists() }) { processName ->
                !viewModel.processes.any { it.name == processName }
            }
        },
        presented = panelState.dialogType is OverviewPanelState.DialogType.CreateProcess,
        onDismissRequest = { panelState.onEvent(OverviewPanelState.Event.CloseDialog) },
        onSelect = {
            EditorScreenEvent.AddProcess(
                name = it
            ).let(viewModel::onEvent)
        }
    )
}

@Composable
fun CreateFunctionDialog(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState
) {
    val localization = Vocabulary.localization

    CreateInvokableDialog(
        inputName = localization.dialogFunctionName(),
        validator = remember(localization) {
            textValidator(error = { localization.functionAlreadyExists() }) { functionName ->
                !viewModel.functions.any { it.name == functionName }
            }
        },
        presented = panelState.dialogType is OverviewPanelState.DialogType.CreateFunction,
        onDismissRequest = { panelState.onEvent(OverviewPanelState.Event.CloseDialog) },
        onSelect = {
            EditorScreenEvent.AddFunction(
                name = it
            ).let(viewModel::onEvent)
        }
    )
}

@Composable
fun CreateLocalVariableDialog(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState
) {
    CreateVariableDialog(
        presented = panelState.dialogType is OverviewPanelState.DialogType.CreateLocalVariable,
        onDismissRequest = { panelState.onEvent(OverviewPanelState.Event.CloseDialog) },
        onSelect = {
            EditorScreenEvent.AddLocalVariable(
                name = it
            ).let(viewModel::onEvent)
        }
    )
}

@Composable
fun CreateGlobalVariableDialog(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState
) {
    CreateVariableDialog(
        presented = panelState.dialogType is OverviewPanelState.DialogType.CreateGlobalVariable,
        onDismissRequest = { panelState.onEvent(OverviewPanelState.Event.CloseDialog) },
        onSelect = {
            EditorScreenEvent.AddGlobalVariable(
                name = it,
                type = Variable.Type.Game
            ).let(viewModel::onEvent)
        }
    )
}

@Composable
fun RenameFunctionDialog(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState
) {
    val localization = Vocabulary.localization
    val onDismiss = { panelState.onEvent(OverviewPanelState.Event.CloseDialog) }

    Sheet(
        presented = panelState.dialogType is OverviewPanelState.DialogType.RenameFunction,
        onDismissRequest = onDismiss
    ) {
        val dialogType = remember { panelState.dialogType as OverviewPanelState.DialogType.RenameFunction }

        RenameInvokableDialog(
            inputName = localization.dialogFunctionName(),
            initialText = { dialogType.function.name },
            validator = remember(localization) {
                textValidator(error = { localization.functionAlreadyExists() }) { functionName ->
                    if (dialogType.function.name == functionName) return@textValidator true

                    return@textValidator !viewModel.functions.any { it.name == functionName }
                }
            },
            onDismissRequest = onDismiss,
            onSelect = {
                EditorScreenEvent.OnFunctionRename(
                    name = it,
                    function = dialogType.function
                ).let(viewModel::onEvent)
            }
        )
    }
}

@Composable
fun RenameProcessDialog(
    viewModel: EditorScreenViewModel,
    panelState: OverviewPanelState
) {
    val localization = Vocabulary.localization
    val onDismiss = { panelState.onEvent(OverviewPanelState.Event.CloseDialog) }

    Sheet(
        presented = panelState.dialogType is OverviewPanelState.DialogType.RenameProcess,
        onDismissRequest = onDismiss
    ) {
        val dialogType = remember { panelState.dialogType as OverviewPanelState.DialogType.RenameProcess }

        RenameInvokableDialog(
            inputName = localization.dialogProcessName(),
            initialText = { dialogType.process.name },
            validator = remember(localization) {
                textValidator(error = { localization.processAlreadyExists() }) { processName ->
                    if (dialogType.process.name == processName) return@textValidator true

                    return@textValidator !viewModel.processes.any { it.name == processName }
                }
            },
            onDismissRequest = onDismiss,
            onSelect = {
                EditorScreenEvent.OnProcessRename(
                    name = it,
                    process = dialogType.process
                ).let(viewModel::onEvent)
            }
        )
    }
}
