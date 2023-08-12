package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.components.Sheet
import com.rodev.jbpkmp.presentation.localization.*
import com.rodev.jbpkmp.presentation.localization.Vocabulary.localization
import com.rodev.jbpkmp.presentation.screens.editor_screen.*

@Composable
fun ResultScreen(
    screenState: EditorScreenState
) {
    val presented by remember { derivedStateOf {
        screenState.result != null && !screenState.isLoading
    } }

    val dismissRequest = remember {
        { screenState.reset() }
    }

    Sheet(
        presented = presented,
        onDismissRequest = dismissRequest
    ) {
        val result = remember { screenState.result }

        when (result) {
            is EditorScreenResult.SuccessUpload -> {
                SuccessUploadScreen(
                    modifier = Modifier,
                    uploadCommand = result.uploadCommand,
                    onDismiss = dismissRequest
                )
            }
            is EditorScreenResult.Error -> {
                ErrorScreen(
                    modifier = Modifier
                        .fillMaxSize(0.5f),
                    error = result,
                    onDismiss = dismissRequest
                )
            }
            is EditorScreenResult.Loading -> {}
            null -> {}
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

            Spacer(modifier = Modifier.height(30.dp))

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
    error: EditorScreenResult.Error,
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
                        text = it,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(columnPadding))

            // Stacktrace text

            StackTraceView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = error.stackTrace ?: "No stacktrace provided",
            )

            Spacer(modifier = Modifier.height(columnPadding))

            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) { focusRequester.requestFocus() }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.End)
                    .focusRequester(focusRequester)
            ) {
                Text(text = localization.ok())
            }
        }
    }
}

@Composable
private fun StackTraceView(
    modifier: Modifier = Modifier,
    text: String
) {
    Box(
        modifier = modifier
    ) {
        val stateVertical = rememberScrollState(0)
        val stateHorizontal = rememberScrollState(0)

        SelectionContainer {
            Text(
                text = text,
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
}

