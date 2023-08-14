package com.rodev.jbpkmp.domain.usecase.upload

import com.rodev.jbpkmp.domain.model.CodeLoadCommand
import com.rodev.jbpkmp.domain.remote.ApiResult
import com.rodev.jbpkmp.domain.remote.CodeUploadService

class CodeUploadUseCase(
    private val codeUploadService: CodeUploadService
) {

    suspend operator fun invoke(data: String): CodeLoadCommand {
        try {
            return when(val result = codeUploadService.upload(data)) {
                is ApiResult.Exception -> throw CodeUploadException(result.exception)
                is ApiResult.Failure -> throw CodeUploadException(result.message)
                is ApiResult.Success -> CodeLoadCommand(result.data.link)
            }
        } catch (e: Throwable) {
            throw CodeUploadException(e)
        }
    }
}

class CodeUploadException : Exception {

    constructor(message: String) : super(message)

    constructor(exception: Throwable) : super(exception)

}