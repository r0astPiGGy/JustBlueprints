package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
