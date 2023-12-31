package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.rodev.jbpkmp.presentation.components.validator.Validators
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.createVariableName
import com.rodev.jbpkmp.presentation.localization.notEmptyErrorMessage

@Composable
fun CreateVariableDialog(
    presented: Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (variableName: String) -> Unit
) {
    val localization = Vocabulary.localization

    val validator = remember(localization) {
        Validators.textNotEmpty { localization.notEmptyErrorMessage() }
    }

    CreateDialogSheet(
        presented = presented,
        inputName = localization.createVariableName(),
        textValidator = validator,
        onDismiss = onDismissRequest,
        onResult = onSelect
    )
}