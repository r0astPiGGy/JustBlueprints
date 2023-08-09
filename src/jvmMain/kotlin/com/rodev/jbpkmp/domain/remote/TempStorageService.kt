package com.rodev.jbpkmp.domain.remote

interface TempStorageService {

    suspend fun upload(data: String): ApiResult<UploadResult>

}

data class UploadResult(
    val commandToLoad: String
)