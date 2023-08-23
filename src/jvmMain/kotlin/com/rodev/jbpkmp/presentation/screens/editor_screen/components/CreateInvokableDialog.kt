package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.rodev.jbpkmp.presentation.components.validator.TextValidator
import com.rodev.jbpkmp.presentation.components.validator.Validators
import com.rodev.jbpkmp.presentation.components.validator.compoundValidatorOf
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.notEmptyErrorMessage

@Composable
fun CreateInvokableDialog(
    presented: Boolean,
    onDismissRequest: () -> Unit,
    inputName: String,
    validator: TextValidator,
    onSelect: (functionName: String) -> Unit
) {
    val localization = Vocabulary.localization

    val compoundValidator = remember(localization) {
        compoundValidatorOf(
            validator,
            Validators.textNotEmpty { localization.notEmptyErrorMessage() }
        )
    }

    CreateDialogSheet(
        presented = presented,
        inputName = inputName,
        textValidator = compoundValidator,
        onDismiss = onDismissRequest,
        onResult = onSelect
    )
}