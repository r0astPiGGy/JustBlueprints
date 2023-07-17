package com.rodev.jbpkmp.presentation.screens.welcome_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.rodev.jbpkmp.data.ProgramDataRepositoryImpl
import com.rodev.jbpkmp.presentation.ResString
import com.rodev.jbpkmp.presentation.screens.welcome_screen.components.FileDialog
import javax.swing.JFileChooser

@Composable
fun WelcomeScreen() {

    val viewModel = WelcomeScreenViewModel(ProgramDataRepositoryImpl())

    Row {
        WelcomePanel(
            modifier = Modifier.weight(2f),
            viewModel = viewModel
        )

        ProjectsPanel(
            modifier = Modifier.weight(1f),
            viewModel = viewModel
        )
    }
}

@Composable
private fun WelcomePanel(
    modifier: Modifier = Modifier,
    viewModel: WelcomeScreenViewModel
) {
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
            onDismissRequest = { isCreateProjectDialogOpen = false },
            viewModel = viewModel
        )
    }

    if (isFileDialogOpen) {
        FileDialog(
            title = ResString.chooseFile,
            type = JFileChooser.OPEN_DIALOG
        ) {
            if (it != null) {
                val event = WelcomeScreenEvent.LoadProject(it)
                viewModel.onEvent(event)
            }
            isFileDialogOpen = false
        }
    }
}

@Composable
private fun ProjectsPanel(
    modifier: Modifier = Modifier,
    viewModel: WelcomeScreenViewModel
) {
    val state = viewModel.state.value

    var selectedProjectPath by remember { mutableStateOf("") }
    val onSelectionChange = { text: String ->
        if (selectedProjectPath == text) {
            println("double click")
        }

        selectedProjectPath = text
    }

    if (state.recentProjects.isNotEmpty()) {
        LazyColumn(
            modifier = modifier
                .fillMaxHeight()
                .background(MaterialTheme.colors.surface)
        ) {
            items(state.recentProjects.size) { index ->
                val path = state.recentProjects[index].project.path
                val selected = path == selectedProjectPath

                ProjectsPanelColumnItem(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            if (path == selectedProjectPath) {
                                MaterialTheme.colors.primary
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable { onSelectionChange(path) },
                    name = state.recentProjects[index].project.name,
                    path = state.recentProjects[index].project.path,
                    selected = selected,
                    onDeleteClick = {
                        val event = WelcomeScreenEvent.RemoveProject(state.recentProjects[index])
                        viewModel.onEvent(event)
                    }
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colors.surface),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ResString.noRecentProjects,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
private fun ProjectsPanelColumnItem(
    modifier: Modifier = Modifier,
    name: String,
    path: String,
    selected: Boolean,
    onDeleteClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.onSurface
            )

            Text(
                text = path,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onSurface
            )
        }

        if (selected) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(25.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CreateProjectDialog(
    onDismissRequest: () -> Unit,
    viewModel: WelcomeScreenViewModel
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
            title = "",
            type = JFileChooser.SAVE_DIALOG,
            selectionMode = JFileChooser.DIRECTORIES_ONLY
        ) {
            if (it != null) {
                val event = WelcomeScreenEvent.CreateProject(projectName, it)
                viewModel.onEvent(event)
            }
            isFileDialogOpen = false
        }
    }
}