package com.rodev.jbpkmp.presentation.components.validator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun ValidatableTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    state: ValidatorState,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    maxLines: Int = 1,
    singleLine: Boolean = true,
) {
    state.onTextChanged = onTextChanged
    state.text = text

    ValidatorContext(
        state = state
    ) {
        Column {
            val error = error

            OutlinedTextField(
                modifier = modifier,
                value = this@ValidatorContext.text,
                onValueChange = ::onTextChange,
                placeholder = placeholder,
                label = label,
                singleLine = singleLine,
                maxLines = maxLines,
                isError = error != null
            )

            val errorModifier = Modifier.padding(top = 5.dp)

            if (error != null) {
                Text(
                    text = error.errorFactory(),
                    modifier = errorModifier,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.h5
                )
            } else {
                Text(
                    text = "",
                    modifier = errorModifier,
                    style = MaterialTheme.typography.h5
                )
            }
        }
    }
}

@Composable
fun ValidatorContext(
    state: ValidatorState,
    content: @Composable ValidatorScope.() -> Unit
) {
    content(state)
}

@Composable
fun rememberValidatorGroup() = remember {
    ValidatorStateGroup()
}

@Composable
fun ValidatorStateGroup.rememberNew(textValidator: TextValidator): ValidatorState {
    return remember { add(textValidator) }
}