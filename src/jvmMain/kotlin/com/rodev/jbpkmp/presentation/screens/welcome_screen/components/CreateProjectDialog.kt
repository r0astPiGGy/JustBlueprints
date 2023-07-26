package com.rodev.jbpkmp.presentation.screens.welcome_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.cancel
import com.rodev.jbpkmp.presentation.localization.chooseDirectory
import com.rodev.jbpkmp.presentation.localization.create
import com.rodev.jbpkmp.presentation.localization.errorMessage
import com.rodev.jbpkmp.presentation.localization.name
import com.rodev.jbpkmp.presentation.screens.welcome_screen.WelcomeScreenEvent
import com.rodev.jbpkmp.presentation.screens.welcome_screen.WelcomeScreenViewModel
import javax.swing.JFileChooser

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateProjectDialog(
    onDismissRequest: () -> Unit,
    viewModel: WelcomeScreenViewModel
) {
    val localization = Vocabulary.localization

    val dialogWidth = 300.dp
    val buttonWidth = 100.dp
    val errorModifier = Modifier.padding(top = 5.dp)

    var projectName by remember { mutableStateOf("") }
    var isFileDialogOpen by remember { mutableStateOf(false) }
    val isError = projectName.isEmpty() && projectName.isBlank()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.width(dialogWidth),
        buttons = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    placeholder = { Text(name(localization)) },
                    singleLine = true,
                    isError = isError
                )

                if (isError) Text(
                    text = errorMessage(localization),
                    modifier = errorModifier,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.h5
                ) else {
                    Text(
                        text = "",
                        modifier = errorModifier,
                        style = MaterialTheme.typography.h5
                    )
                }

                Spacer(Modifier.height(25.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier.width(buttonWidth),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
                    ) {
                        Text(cancel(localization))
                    }

                    Button(
                        onClick = { isFileDialogOpen = true },
                        modifier = Modifier.width(buttonWidth)
                    ) {
                        Text(create(localization))
                    }
                }
            }
        }
    )

    if (isFileDialogOpen && projectName.isNotEmpty() && projectName.isNotBlank()) {
        FileDialog(
            title = chooseDirectory(localization),
            type = JFileChooser.SAVE_DIALOG,
            selectionMode = JFileChooser.DIRECTORIES_ONLY
        ) {
            if (it != null) {
                val event = WelcomeScreenEvent.CreateProject(projectName, it)
                viewModel.onEvent(event)

                onDismissRequest()
            }
            isFileDialogOpen = false
        }
    }
}