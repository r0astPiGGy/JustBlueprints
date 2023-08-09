package com.rodev.jbpkmp.data

import com.rodev.jbpkmp.domain.remote.ApiResult
import com.rodev.jbpkmp.domain.remote.TempStorageService
import com.rodev.jbpkmp.domain.remote.UploadResult
import kotlinx.coroutines.delay

class TempStorageServiceImpl : TempStorageService {

    override suspend fun upload(data: String): ApiResult<UploadResult> {
        delay(2000)

        return ApiResult.Success(
            data = UploadResult(
                commandToLoad = "/test command"
            )
        )
    }

}