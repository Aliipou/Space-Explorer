package com.example.spaceexplorer.domain.model

sealed class SpaceResult<out T> {
    data object Loading : SpaceResult<Nothing>()
    data class Success<T>(val data: T, val fromCache: Boolean = false) : SpaceResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : SpaceResult<Nothing>()
}

inline fun <T> SpaceResult<T>.onSuccess(action: (T) -> Unit): SpaceResult<T> {
    if (this is SpaceResult.Success) action(data)
    return this
}

inline fun <T> SpaceResult<T>.onError(action: (String) -> Unit): SpaceResult<T> {
    if (this is SpaceResult.Error) action(message)
    return this
}

inline fun <T> SpaceResult<T>.onLoading(action: () -> Unit): SpaceResult<T> {
    if (this is SpaceResult.Loading) action()
    return this
}
