package com.rodev.jbpkmp.data

import com.rodev.jbpkmp.domain.remote.ApiResult
import com.rodev.jbpkmp.domain.remote.CodeUploadService
import com.rodev.jbpkmp.domain.remote.UploadResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

private const val BASE_URL = "https://m.justmc.ru"

class CodeUploadServiceImpl : CodeUploadService {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    override suspend fun upload(data: String): ApiResult<UploadResult> {
        val uploadUrl = "$BASE_URL/api/upload"

        val response = httpClient.post(uploadUrl) {
            setBody(data)
            accept(ContentType.Application.Json)
        }

        try {
            return if (response.status.value in 200..299) {
                val id = response.body<UploadResponse>().id

                ApiResult.Success(
                    data = UploadResult(
                        commandToLoad = "/module load force $BASE_URL/api/$id"
                    )
                )
            } else {
                val error = response.body<UploadErrorResponse>().error

                ApiResult.Failure(
                    message = error
                )
            }
        } catch (e: Throwable) {
            return ApiResult.Exception(
                exception = e
            )
        }
    }

    @Serializable
    data class UploadResponse(
        val id: String
    )

    @Serializable
    data class UploadErrorResponse(
        val error: String
    )

}