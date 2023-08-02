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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.components.Sheet
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.cancel
import com.rodev.jbpkmp.presentation.localization.chooseDirectory
import com.rodev.jbpkmp.presentation.localization.create
import com.rodev.jbpkmp.presentation.localization.errorMessage
import com.rodev.jbpkmp.presentation.localization.name
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
    val isError = projectName.isEmpty() && projectName.isBlank()

    Sheet(presented) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.surface)
                .padding(10.dp),
        ) {
            val errorModifier = Modifier.padding(top = 5.dp)

            OutlinedTextField(
                value = projectName,
                onValueChange = { projectName = it },
                placeholder = { Text(localization.name()) },
                singleLine = true,
                isError = isError
            )

            if (isError) Text(
                text = localization.errorMessage(),
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
                val buttonWidth = 100.dp

                OutlinedButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.width(buttonWidth)
                ) {
                    Text(localization.cancel())
                }

                Button(
                    onClick = { isFileDialogOpen = true },
                    modifier = Modifier.width(buttonWidth)
                ) {
                    Text(localization.create())
                }
            }
        }
    }

    if (isFileDialogOpen && !isError) {
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