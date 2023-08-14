package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.components.HorizontalDivider
import com.rodev.jbpkmp.presentation.components.MaterialIconButton
import com.rodev.jbpkmp.presentation.components.Sheet
import com.rodev.jbpkmp.presentation.components.VerticalDivider
import com.rodev.jbpkmp.presentation.localization.Vocabulary.localization
import com.rodev.jbpkmp.presentation.localization.codeUpload
import com.rodev.jbpkmp.presentation.localization.projectCompile
import com.rodev.jbpkmp.presentation.localization.projectSave
import com.rodev.jbpkmp.presentation.navigation.NavController
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.*
import com.rodev.jbpkmp.presentation.screens.settings_screen.SettingsScreen
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun EditorScreen(navController: NavController, projectPath: String) {
    val viewModel: EditorScreenViewModel = koinInject { parametersOf(projectPath) }
    val state = remember { viewModel.state }

    // Auto-save
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onDispose()
        }
    }

    LaunchedEffect(state.navigationResult) {
        when (state.navigationResult) {
            NavigationResult.GoBack -> {
                navController.navigateBack()
                state.reset()
            }
            NavigationResult.Empty -> {}
        }
    }

    Surface(
        modifier = Modifier
            .onKeyEvent(viewModel::handleKeyEvent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopBarPanel(
                modifier = Modifier.fillMaxWidth(),
                viewModel
            )

            HorizontalDivider()

            DraggableContext(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    VerticalDivider()

                    RightPanel(
                        modifier = Modifier
                            .requiredWidth(300.dp),
                        viewModel
                    )

                    VerticalDivider()

                    val currentGraph = viewModel.currentGraph

                    if (currentGraph != null) {
                        ViewPortPanel(viewModel, currentGraph)
                    }
                }
            }

            HorizontalDivider()

            ActionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(MaterialTheme.colors.background),
                screenState = state
            )
        }

        ResultScreen(screenState = state)

        Sheet(
            presented = state.showSettingsScreen,
            onDismissRequest = { viewModel.onEvent(EditorScreenEvent.CloseSettingsScreen) }
        ) {
            SettingsScreen(
                onDismissRequest = { viewModel.onEvent(EditorScreenEvent.CloseSettingsScreen) },
                modifier = Modifier.fillMaxSize(0.7f)
            )
        }
    }
}

@Composable
fun TopBarPanel(
    modifier: Modifier = Modifier,
    viewModel: EditorScreenViewModel,
) {
    ToolBar(
        modifier = modifier,
        startContent = {
            // Back button
            MaterialIconButton(
                imageVector = Icons.Default.ArrowBack,
                onClick = { viewModel.onEvent(EditorScreenEvent.CloseProject) }
            )

            // Settings button
            MaterialIconButton(
                imageVector = Icons.Default.Settings,
                onClick = { viewModel.onEvent(EditorScreenEvent.OpenSettingsScreen) }
            )
        },
        centerContent = {
            Text(text = viewModel.project.name)
        },
        endContent = {
            // Build button
            MaterialIconButton(
                imageVector = Icons.Default.Build,
                enabled = !viewModel.state.isLoading,
                onClick = { viewModel.onEvent(EditorScreenEvent.BuildProject) }
            )

            // Save button
            MaterialIconButton(
                imageVector = Icons.Default.Save,
                enabled = !viewModel.state.isLoading,
                onClick = { viewModel.onEvent(EditorScreenEvent.SaveProject) }
            )
        }
    )
}

@Composable
fun RightPanel(
    modifier: Modifier = Modifier,
    viewModel: EditorScreenViewModel
) {
    Column(
        modifier = modifier
    ) {
        OverviewPanel(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            viewModel = viewModel
        )

        HorizontalDivider()

        DetailsPanel(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            viewModel = viewModel
        )
    }
}

@Composable
fun ViewPortPanel(
    viewModel: EditorScreenViewModel,
    currentGraph: GraphState
) {
    DropTarget<VariableState>(
        modifier = Modifier
    ) { isInBound, data, position ->
        data?.let {
            if (isInBound) {
                viewModel.onEvent(
                    EditorScreenEvent.OnDragAndDrop(
                        data,
                        position
                    )
                )
            }
        }

        BlueprintViewPort(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isInBound) {
                        Modifier.border(4.dp, color = Color.Blue)
                    } else {
                        Modifier
                    }
                ),
            viewModel = currentGraph.viewModel
        )
    }
}

@Composable
fun ActionBar(
    modifier: Modifier = Modifier,
    screenState: EditorScreenState
) {
    Row(
        modifier = modifier
            .padding(10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val result = screenState.result

        if (result is EditorScreenResult.Loading) {
            val message = when (result.state) {
                LoadingState.SAVE -> localization.projectSave()
                LoadingState.COMPILE -> localization.projectCompile()
                LoadingState.UPLOAD -> localization.codeUpload()
            }

            Text(text = message)

            Spacer(modifier = Modifier.width(10.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(300.dp),
            )
        }
    }
}
