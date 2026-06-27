package com.olio.trainsembleapp.cars

import android.os.Message

sealed class CarsUiState {
    data class Loading(val message: String? = null) : CarsUiState()
    data class Success(val data: List<Car>) : CarsUiState()
    data class Error(val exception: Exception) : CarsUiState()
}