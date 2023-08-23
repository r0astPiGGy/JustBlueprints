package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.rodev.jbpkmp.presentation.components.validator.TextValidator
import com.rodev.jbpkmp.presentation.components.validator.Validators
import com.rodev.jbpkmp.presentation.components.validator.compoundValidatorOf
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.applyText
import com.rodev.jbpkmp.presentation.localization.notEmptyErrorMessage

@Composable
fun RenameInvokableDialog(
    onDismissRequest: () -> Unit,
    initialText: () -> String = { "" },
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

    CreateDialog(
        inputName = inputName,
        initialText = initialText,
        textValidator = compoundValidator,
        onDismiss = onDismissRequest,
        applyButtonName = localization.applyText(),
        onResult = onSelect
    )
}