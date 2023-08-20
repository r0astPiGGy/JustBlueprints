package com.rodev.jbpkmp.presentation.components.validator

object Validators {

    fun textNotEmpty(errorMessageFactory: ErrorMessageFactory): TextValidator {
        return textValidator(errorMessageFactory) {
            it.isNotEmpty()
        }
    }

    fun regex(regex: Regex, errorMessageFactory: ErrorMessageFactory): TextValidator {
        return textValidator(errorMessageFactory) {
            regex.matches(it)
        }
    }

    fun textInRange(range: IntRange, errorMessageFactory: ErrorMessageFactory): TextValidator {
        return textValidator(errorMessageFactory) {
            it.length in range
        }
    }

    fun textMax(maxLength: Int, errorMessageFactory: ErrorMessageFactory): TextValidator {
        return textValidator(errorMessageFactory) {
            it.length < maxLength
        }
    }

}