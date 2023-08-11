package com.rodev.jbpkmp.data

import com.rodev.jbpkmp.domain.remote.ApiResult
import com.rodev.jbpkmp.domain.remote.CodeUploadService
import com.rodev.jbpkmp.domain.remote.UploadResult
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://tmpfiles.org/api/v1/upload"

class FallbackCodeUploadService : CodeUploadService {


    private val httpClient = HttpClient(CIO)
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun upload(data: String): ApiResult<UploadResult> {
        val response = httpClient.post(BASE_URL) {
            accept(ContentType.Application.Json)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", data.toByteArray(), Headers.build {
                            append(HttpHeaders.ContentType, "text/plain")
                            append(HttpHeaders.ContentDisposition, "filename=\"output.json\"")
                        })
                    }
                )
            )
        }

        val link = response
            .jsonBody<ApiResponse>()
            .data
            .url
            .replace(
                "tmpfiles.org/",
                "tmpfiles.org/dl/"
            )

        return ApiResult.Success(
            data = UploadResult(
                commandToLoad = "/module loadUrl force $link"
            )
        )
    }

    private suspend inline fun <reified T> HttpResponse.jsonBody(): T {
        return bodyAsText().let {
            println(it)
            json.decodeFromString(it)
        }
    }

}

@Serializable
private data class ApiResponse(
    val status: String,
    val data: Data
) {

    @Serializable
    data class Data(
        val url: String
    )

}