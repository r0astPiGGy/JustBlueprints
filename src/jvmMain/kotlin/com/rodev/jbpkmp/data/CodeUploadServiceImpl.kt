package com.rodev.jbpkmp.data

import com.rodev.jbpkmp.domain.remote.ApiResult
import com.rodev.jbpkmp.domain.remote.CodeUploadService
import com.rodev.jbpkmp.domain.remote.UploadResult
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://m.justmc.ru"

class CodeUploadServiceImpl : CodeUploadService {

    private val httpClient = HttpClient(CIO)
    private val json = Json

    private val fallbackService = FallbackCodeUploadService()

    override suspend fun upload(data: String): ApiResult<UploadResult> {
        val uploadUrl = "$BASE_URL/api/upload"

        val response = httpClient.post(uploadUrl) {
            setBody(data)
            accept(ContentType.Application.Json)
        }

        try {
            return if (response.status.value in 200..299) {
                val id = response.jsonBody<UploadResponse>().id

                ApiResult.Success(
                    data = UploadResult(
                        commandToLoad = "/module loadUrl force $BASE_URL/api/$id"
                    )
                )
            } else {
                val error = response.jsonBody<UploadErrorResponse>().error

                ApiResult.Failure(
                    message = error
                )
            }
        } catch (e: Throwable) {
            return try {
                fallbackService.upload(data)
            } catch (ignored: Exception) {
                ApiResult.Exception(e)
            }
        }
    }

    private suspend inline fun <reified T> HttpResponse.jsonBody(): T {
        return json.decodeFromString(bodyAsText())
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