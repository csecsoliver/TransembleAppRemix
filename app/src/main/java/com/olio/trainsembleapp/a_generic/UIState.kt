package com.olio.trainsembleapp.a_generic

sealed class UiState<T> {
    data class Loading<T>(val message: String? = null) : UiState<T>()
    data class Success<T>(val data: List<T>) : UiState<T>()
    data class Error<T>(val exception: Exception) : UiState<T>()
}

val <T> UiState<T>.isError: Boolean get() = this is UiState.Error