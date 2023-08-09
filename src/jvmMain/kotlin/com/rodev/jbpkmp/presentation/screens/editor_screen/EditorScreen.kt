package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodev.jbpkmp.ViewPortPreview
import com.rodev.jbpkmp.data.ProgramDataRepositoryImpl
import com.rodev.jbpkmp.domain.remote.UploadResult
import com.rodev.jbpkmp.domain.repository.update
import com.rodev.jbpkmp.presentation.components.Sheet
import com.rodev.jbpkmp.presentation.localization.*
import com.rodev.jbpkmp.presentation.localization.Vocabulary.localization
import com.rodev.jbpkmp.presentation.navigation.NavController
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.Details
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.DraggableContext
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.DropTarget
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.Overview
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.ToolBar
import com.rodev.jbpkmp.presentation.screens.settings_screen.SettingsScreen
import com.rodev.jbpkmp.presentation.screens.settings_screen.SettingsScreenEvent
import org.jetbrains.skia.paragraph.TextBox

@Composable
fun EditorScreen(navController: NavController, projectPath: String) {
    val viewModel = remember { EditorScreenViewModel(projectPath) }

    // Auto-save
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onDispose()
        }
    }

    Surface(
        modifier = Modifier
            .onKeyEvent(viewModel::handleKeyEvent)
    ) {
        var showSettingsScreen by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ToolBar(
                modifier = Modifier.fillMaxWidth(),
                startContent = {
                    // Back button
                    MaterialIconButton(
                        imageVector = Icons.Default.ArrowBack,
                        onClick = {
                            ProgramDataRepositoryImpl().update {
                                settings.lastOpenProjectPath = null
                            }
                            navController.navigateBack()
                        }
                    )

                    // Settings button
                    MaterialIconButton(
                        imageVector = Icons.Default.Settings,
                        onClick = { showSettingsScreen = true }
                    )
                },
                centerContent = {
                    // TODO
//                    viewModel.currentGraph?.let {
//                        Text(text = viewModel.project.name + it.name)
//                    }
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

            HorizontalDivider()

            DraggableContext(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    VerticalDivider()

                    Column(
                        modifier = Modifier
                            .requiredWidth(300.dp)
                    ) {

                        Overview(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            viewModel = viewModel
                        )

                        HorizontalDivider()

                        Details(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            viewModel = viewModel
                        )
                    }

                    VerticalDivider()

                    val currentGraph = viewModel.currentGraph

                    if (currentGraph != null) {
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

                            ViewPortPreview(
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
                }
            }

            HorizontalDivider()

            ActionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(MaterialTheme.colors.background),
                screenState = viewModel.state
            )
        }

        ResultDialog(screenState = viewModel.state)

        Sheet(showSettingsScreen) {
            SettingsScreen(
                onDismissRequest = { showSettingsScreen = false },
                modifier = Modifier.fillMaxSize(0.7f)
            )
        }
    }
}

@Composable
fun ResultDialog(
    screenState: EditorScreenState
) {
    if (screenState.isLoading) return
    val result = screenState.result ?: return
    val dismissRequest = remember {
        { screenState.reset() }
    }

    Sheet(
        true,
        onDismissRequest = dismissRequest
    ) {
        when (result) {
            is ScreenResult.SuccessUpload -> {
                SuccessUploadScreen(
                    modifier = Modifier,
                    uploadCommand = result.uploadCommand,
                    onDismiss = dismissRequest
                )
            }
            is ScreenResult.Error -> {
                ErrorScreen(
                    modifier = Modifier
                        .fillMaxSize(0.5f),
                    error = result,
                    onDismiss = dismissRequest
                )
            }
            is ScreenResult.Loading -> {}
        }

    }
}

@Composable
fun SuccessUploadScreen(
    modifier: Modifier = Modifier,
    uploadCommand: String,
    onDismiss: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        val columnPadding = 15.dp

        Column(
            modifier = Modifier
                .padding(columnPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = localization.uploadSuccess(),
                    fontSize = MaterialTheme.typography.h3.fontSize
                )
                Text(
                    text = localization.uploadHint()
                )
            }

            Spacer(modifier = Modifier.height(columnPadding))

            val clipboard = LocalClipboardManager.current

            Button(
                onClick = {
                    clipboard.setText(AnnotatedString(uploadCommand))
                    onDismiss()
                },
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.End)
            ) {
                Text(text = localization.copyButton())
            }
        }
    }
}

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    error: ScreenResult.Error,
    onDismiss: () -> Unit
) {
    val title = when (error.stage) {
        LoadingState.UPLOAD -> localization.uploadError()
        LoadingState.SAVE -> localization.saveError()
        LoadingState.COMPILE -> localization.compileError()
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        val columnPadding = 15.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(columnPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.h3.fontSize
                )
                error.message?.let {
                    Text(
                        text = it
                    )
                }
            }

            Spacer(modifier = Modifier.height(columnPadding))

            // Stacktrace text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val stateVertical = rememberScrollState(0)
                val stateHorizontal = rememberScrollState(0)

                SelectionContainer {
                    Text(
                        text = error.stackTrace ?: "No stacktrace provided",
                        color = MaterialTheme.colors.error,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .verticalScroll(stateVertical)
                            .padding(end = 12.dp, bottom = 12.dp)
                            .horizontalScroll(stateHorizontal)
                    )
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(stateVertical)
                )
                HorizontalScrollbar(
                    modifier = Modifier.align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(end = 12.dp),
                    adapter = rememberScrollbarAdapter(stateHorizontal)
                )
            }

            Spacer(modifier = Modifier.height(columnPadding))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.End)
            ) {
                Text(text = localization.ok())
            }
        }
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

        if (result is ScreenResult.Loading) {
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

const val dividerSize = 3

@Composable
fun VerticalDivider() {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .width(dividerSize.dp),
        color = Color.Black
    )
}

@Composable
fun HorizontalDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(dividerSize.dp),
        color = Color.Black
    )
}

@Composable
fun MaterialIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imageVector: ImageVector,
    onClick: () -> Unit = {},
) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}