package com.rodev.jbpkmp.presentation.components.validator

fun interface Validator<T> {

    fun validate(obj: T): ValidateResult

}

typealias TextValidator = Validator<String>

fun <T> compoundValidatorOf(validator: Validator<T>, vararg validators: Validator<T>): Validator<T> {
    if (validators.isEmpty()) return validator

    return CompoundValidator(
        mutableListOf(validator).also { it.addAll(validators) }
    )
}

fun textValidator(error: ErrorMessageFactory, onValidate: (String) -> Boolean): TextValidator {
    return TextValidatorImpl(error, onValidate)
}

private class TextValidatorImpl(
    error: ErrorMessageFactory,
    private val onValidate: (String) -> Boolean
) : TextValidator {

    private val failure = ValidateResult.Failure(error)

    override fun validate(obj: String): ValidateResult {
        return if (onValidate(obj)) {
            ValidateResult.Success
        } else {
            failure
        }
    }

}

private class CompoundValidator<T>(
    private val validators: List<Validator<T>>
) : Validator<T> {

    override fun validate(obj: T): ValidateResult {
        for (validator in validators) {
            val result = validator.validate(obj)

            if (result.isFailure()) {
                return result
            }
        }
        return ValidateResult.Success
    }
}