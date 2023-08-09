package com.rodev.jbpkmp.domain.remote

sealed class ApiResult<T> {

    class Success<T>(val data: T) : ApiResult<T>()

    class Failure<T>(val message: String): ApiResult<T>()

    class Exception<T>(val exception: Throwable): ApiResult<T>()

}