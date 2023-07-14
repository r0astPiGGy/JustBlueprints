package com.rodev.jbpkmp.presentation.screens.welcome_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.ResString
import com.rodev.jbpkmp.presentation.screens.welcome_screen.components.FileDialog

@Composable
fun WelcomeScreen() {
    Row {
        WelcomePanel(modifier = Modifier.weight(2f))
        ProjectsPanel(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun WelcomePanel(modifier: Modifier = Modifier) {
    val buttonWidth = 300.dp
    val spacerHeight = 25.dp

    var isCreateProjectDialogOpen by remember { mutableStateOf(false) }

    var isFileDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource("images/logo.png"),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(20.dp))
        )

        Spacer(Modifier.height(spacerHeight))

        Text(
            text = ResString.appName,
            style = MaterialTheme.typography.h2
        )

        Text(ResString.authors)

        Spacer(Modifier.height(spacerHeight))

        Button(
            onClick = { isCreateProjectDialogOpen = true },
            modifier = Modifier.width(buttonWidth)
        ) {
            Text(ResString.createNewProject)
        }

        Button(
            onClick = { isFileDialogOpen = true },
            modifier = Modifier.width(buttonWidth)
        ) {
            Text(ResString.openProject)
        }
    }

    if (isCreateProjectDialogOpen) {
        CreateProjectDialog(
            onDismissRequest = { isCreateProjectDialogOpen = false }
        )
    }

    if (isFileDialogOpen) {
        FileDialog(
            openParam = java.awt.FileDialog.LOAD,
            onCloseRequest = {
                isFileDialogOpen = false
                println("Result $it")
            }
        )
    }
}

@Composable
private fun ProjectsPanel(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colors.surface)
    ) {
        items(10) {
            ProjectsPanelColumnItem(
                modifier = Modifier.padding(5.dp),
                title = "Projects",
                path = "Path"
            )
        }
    }
}

@Composable
private fun ProjectsPanelColumnItem(
    modifier: Modifier = Modifier,
    title: String,
    path: String
) {
    val selected = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(
                if (selected.value) {
                    MaterialTheme.colors.primary
                } else {
                    Color.Transparent
                }
            )
            .padding(5.dp)
            .selectable(
                selected = selected.value,
                onClick = { selected.value = !selected.value }
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onSurface
        )

        Text(
            text = path,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CreateProjectDialog(
    onDismissRequest: () -> Unit
) {
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
                    .background(MaterialTheme.colors.background)
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    placeholder = { Text(ResString.name) },
                    singleLine = true,
                    isError = isError
                )

                if (isError) Text(
                    text = ResString.errorMessage,
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
                        Text(ResString.cancel)
                    }

                    Button(
                        onClick = { isFileDialogOpen = true },
                        modifier = Modifier.width(buttonWidth)
                    ) {
                        Text(ResString.create)
                    }
                }
            }
        }
    )

    if (isFileDialogOpen && projectName.isNotEmpty() && projectName.isNotBlank()) {
        FileDialog(
            openParam = java.awt.FileDialog.SAVE,
            fileName = projectName,
            onCloseRequest = { isFileDialogOpen = false }
        )
    }
}