package com.rodev.jbpkmp.presentation.screens.welcome_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.invalidProject
import com.rodev.jbpkmp.presentation.navigation.NavController
import com.rodev.jbpkmp.presentation.navigation.Screen
import com.rodev.jbpkmp.presentation.navigation.argumentBundleOf
import com.rodev.jbpkmp.presentation.screens.welcome_screen.components.ProjectsPanel
import com.rodev.jbpkmp.presentation.screens.welcome_screen.components.WelcomePanel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun WelcomeScreen(navController: NavController) {
    val viewModel = koinInject<WelcomeScreenViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val localization = Vocabulary.localization

    Box {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            WelcomePanel(
                modifier = Modifier.weight(2f),
                viewModel = viewModel,
            )

            ProjectsPanel(
                modifier = Modifier.weight(1f),
                state = viewModel.projectsPanelState,
                onProjectOpen = { viewModel.onEvent(WelcomeScreenEvent.OpenProject(it)) },
                onProjectDelete = { viewModel.onEvent(WelcomeScreenEvent.RemoveProject(it)) }
            )
        }
        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(400.dp),
            hostState = snackbarHostState
        )
    }

    LaunchedEffect(viewModel.state.result) {
        viewModel.state.result?.let {
            when (it) {
                is WelcomeScreenResult.Failure -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            localization.invalidProject()
                        )
                    }
                    viewModel.state.consume()
                }

                is WelcomeScreenResult.OpenProject -> {
                    navController.navigate(Screen.EditorScreen.name, argumentBundleOf {
                        putString("projectPath", it.projectPath)
                    })
                    viewModel.state.consume()
                }
            }
        }
    }
}