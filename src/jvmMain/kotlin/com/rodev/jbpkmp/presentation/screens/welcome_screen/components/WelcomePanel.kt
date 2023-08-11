package com.rodev.jbpkmp.presentation.screens.welcome_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.localization.*
import com.rodev.jbpkmp.presentation.screens.welcome_screen.WelcomeScreenEvent
import com.rodev.jbpkmp.presentation.screens.welcome_screen.WelcomeScreenViewModel
import javax.swing.JFileChooser

@Composable
fun WelcomePanel(
    modifier: Modifier = Modifier,
    viewModel: WelcomeScreenViewModel
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
                WelcomeScreenEvent.LoadAndOpenProject(it).let(viewModel::onEvent)
            }

            isFileDialogOpen = false
        }
    }
}