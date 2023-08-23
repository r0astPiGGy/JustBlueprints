package com.rodev.jbpkmp.presentation.screens.welcome_screen.components

import androidx.compose.runtime.*
import com.rodev.jbpkmp.presentation.components.validator.Validators
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.chooseDirectory
import com.rodev.jbpkmp.presentation.localization.createProjectName
import com.rodev.jbpkmp.presentation.localization.notEmptyErrorMessage
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.CreateDialogSheet
import com.rodev.jbpkmp.presentation.screens.welcome_screen.WelcomeScreenEvent
import com.rodev.jbpkmp.presentation.screens.welcome_screen.WelcomeScreenViewModel
import javax.swing.JFileChooser

@Composable
fun CreateProjectDialog(
    presented: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: WelcomeScreenViewModel
) {
    val localization = Vocabulary.localization

    var projectName by remember { mutableStateOf("") }
    var isFileDialogOpen by remember { mutableStateOf(false) }

    CreateDialogSheet(
        presented = presented,
        inputName = createProjectName(localization),
        textValidator = Validators.textNotEmpty { localization.notEmptyErrorMessage() },
        onDismiss = onDismissRequest,
        onResult = {
            projectName = it
            isFileDialogOpen = true
        }
    )

    if (isFileDialogOpen) {
        FileDialog(
            title = localization.chooseDirectory(),
            type = JFileChooser.SAVE_DIALOG,
            selectionMode = JFileChooser.DIRECTORIES_ONLY
        ) {
            if (it != null) {
                WelcomeScreenEvent.CreateAndOpenProject(projectName, it)
                    .let(viewModel::onEvent)

                onDismissRequest()
            }
            isFileDialogOpen = false
        }
    }
}