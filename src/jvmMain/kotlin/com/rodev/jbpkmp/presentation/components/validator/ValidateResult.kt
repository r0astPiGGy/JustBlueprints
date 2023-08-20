package com.rodev.jbpkmp.presentation.components.validator

sealed interface ValidateResult {

    object Success : ValidateResult
    data class Failure(val errorFactory: ErrorMessageFactory): ValidateResult

}

fun ValidateResult.isSuccess() = !isFailure()

fun ValidateResult.isFailure() = this !is ValidateResult.Success

typealias ErrorMessageFactory = () -> String