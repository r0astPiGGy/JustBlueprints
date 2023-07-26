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
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
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
import com.rodev.jbpkmp.data.ProgramDataRepositoryImpl
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.appName
import com.rodev.jbpkmp.presentation.localization.authors
import com.rodev.jbpkmp.presentation.localization.chooseFile
import com.rodev.jbpkmp.presentation.localization.createNewProject
import com.rodev.jbpkmp.presentation.localization.noRecentProjects
import com.rodev.jbpkmp.presentation.localization.openProject
import com.rodev.jbpkmp.presentation.navigation.NavController
import com.rodev.jbpkmp.presentation.navigation.Screen
import com.rodev.jbpkmp.presentation.screens.welcome_screen.components.CreateProjectDialog
import com.rodev.jbpkmp.presentation.screens.welcome_screen.components.FileDialog
import com.rodev.jbpkmp.presentation.screens.welcome_screen.components.RecentProjectItem
import javax.swing.JFileChooser

@Composable
fun WelcomeScreen(navController: NavController) {
    val viewModel = WelcomeScreenViewModel(ProgramDataRepositoryImpl())

    Row {
        WelcomePanel(
            modifier = Modifier.weight(2f),
            viewModel = viewModel,
            navController = navController
        )

        ProjectsPanel(
            modifier = Modifier.weight(1f),
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
private fun WelcomePanel(
    modifier: Modifier = Modifier,
    viewModel: WelcomeScreenViewModel,
    navController: NavController
) {
    val localization = Vocabulary.localization

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
            text = appName,
            style = MaterialTheme.typography.h2
        )

        Text(localization.authors())

        Spacer(Modifier.height(spacerHeight))

        Button(
            onClick = { isCreateProjectDialogOpen = true },
            modifier = Modifier.width(buttonWidth)
        ) {
            Text(localization.createNewProject())
        }

        Button(
            onClick = { isFileDialogOpen = true },
            modifier = Modifier.width(buttonWidth)
        ) {
            Text(localization.openProject())
        }
    }

    CreateProjectDialog(
        presented = isCreateProjectDialogOpen,
        onDismissRequest = { isCreateProjectDialogOpen = false },
        viewModel = viewModel
    )

    if (isFileDialogOpen) {
        FileDialog(
            title = localization.chooseFile(),
            type = JFileChooser.OPEN_DIALOG
        ) {
            if (it != null) {
                WelcomeScreenEvent.LoadProject(it).let(viewModel::onEvent)
                navController.navigate(Screen.EditorScreen.name)
            }

            isFileDialogOpen = false
        }
    }
}

@Composable
private fun ProjectsPanel(
    modifier: Modifier = Modifier,
    viewModel: WelcomeScreenViewModel,
    navController: NavController
) {
    val state = viewModel.state.value

    var selectedProjectPath by remember { mutableStateOf("") }
    val onSelectionChange = { text: String ->
        if (selectedProjectPath == text) {
            navController.navigate(Screen.EditorScreen.name)
        }

        selectedProjectPath = text
    }

    if (state.recentProjects.isNotEmpty()) {
        LazyColumn(
            modifier = modifier
                .fillMaxHeight()
                .background(MaterialTheme.colors.background)
        ) {
            items(state.recentProjects.size) { index ->
                val path = state.recentProjects[index].project.path
                val selected = path == selectedProjectPath

                RecentProjectItem(
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
                        WelcomeScreenEvent.RemoveProject(state.recentProjects[index])
                            .let(viewModel::onEvent)
                    }
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = Vocabulary.localization.noRecentProjects(),
                modifier = Modifier.padding(horizontal = 10.dp),
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}