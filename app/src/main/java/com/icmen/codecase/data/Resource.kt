package com.icmen.codecase.data


sealed class Resource<T>(
    val data: T? = null,
    val error: String? = null
) {
    class Loading<T> : Resource<T>()
    class Success<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
