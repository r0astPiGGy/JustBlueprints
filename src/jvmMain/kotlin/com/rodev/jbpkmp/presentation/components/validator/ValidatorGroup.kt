package com.rodev.jbpkmp.presentation.components.validator

import androidx.compose.runtime.*

class ValidatorStateGroup {

    private val validators = mutableListOf<ValidatorState>()

    val success by derivedStateOf {
        !validators.map { it.forceValidate() }.contains(false)
    }

    fun validate(): Boolean {
        var validated = true

        for (validator in validators) {
            if (!validator.forceValidate() && validated) {
                validated = false
            }
        }

        return validated
    }

    fun add(textValidator: TextValidator): ValidatorState {
        return textValidator.let(::ValidatorState).also(validators::add)
    }

}

class ValidatorState(
    private val validator: TextValidator
): ValidatorScope {

    override var text by mutableStateOf("")
    private var lastValidateResult: ValidateResult by mutableStateOf(ValidateResult.Success)

    var onTextChanged: (String) -> Unit = {}

    override val error by derivedStateOf {
        lastValidateResult as? ValidateResult.Failure
    }

    override fun onTextChange(text: String) {
        this.text = text
        onTextChanged(text)
        forceValidate()
    }

    fun forceValidate(): Boolean {
        lastValidateResult = validator.validate(text)
        return lastValidateResult.isSuccess()
    }

}

interface ValidatorScope {

    val text: String
    val error: ValidateResult.Failure?

    fun onTextChange(text: String)

}
