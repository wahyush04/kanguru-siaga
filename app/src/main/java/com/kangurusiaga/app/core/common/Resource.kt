package com.kangurusiaga.app.core.common

sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val throwable: Throwable, val message: String? = throwable.localizedMessage) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}
