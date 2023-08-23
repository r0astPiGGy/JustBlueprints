package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.components.Sheet
import com.rodev.jbpkmp.presentation.components.validator.*
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.cancel
import com.rodev.jbpkmp.presentation.localization.create

@Composable
fun CreateDialogSheet(
    presented: Boolean,
    initialText: () -> String = { "" },
    inputName: String,
    textValidator: TextValidator,
    applyButtonName: String = Vocabulary.localization.create(),
    onDismiss: () -> Unit,
    onResult: (String) -> Unit
) {
    Sheet(
        presented = presented,
        onDismissRequest = onDismiss
    ) {
        CreateDialog(
            initialText,
            inputName,
            textValidator,
            applyButtonName,
            onDismiss,
            onResult
        )
    }
}

@Composable
fun CreateDialog(
    initialText: () -> String = { "" },
    inputName: String,
    textValidator: TextValidator,
    applyButtonName: String,
    onDismiss: () -> Unit,
    onResult: (String) -> Unit
) {
    var result by remember { mutableStateOf(initialText()) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Card(
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .background(MaterialTheme.colors.surface)
                .padding(10.dp)
        ) {
            val validator = rememberValidatorGroup()

            val onResultWrapper = {
                if (validator.success) {
                    onDismiss()
                    onResult(result)
                }
            }

            ValidatableTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onEnter(onResultWrapper)
                    .fillMaxWidth(),
                text = result,
                onTextChanged = { result = it },
                placeholder = { Text(inputName) },
                state = validator.rememberNew(textValidator)
            )

            Spacer(Modifier.height(15.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = MaterialTheme.colors.surface
                    )
                ) {
                    Text(Vocabulary.localization.cancel())
                }

                Spacer(
                    modifier = Modifier.width(75.dp)
                )

                Button(
                    onClick = onResultWrapper,
                    enabled = validator.success,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(applyButtonName)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onEnter(action: () -> Unit): Modifier {
    return this.onKeyEvent {
        if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
            action()
            return@onKeyEvent true
        } else {
            return@onKeyEvent false
        }
    }
}