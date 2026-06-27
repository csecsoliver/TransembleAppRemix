package com.olio.trainsembleapp.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarsViewModel(
    private val repository: CarsRepository
) : ViewModel() {

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    private val _uiState = MutableStateFlow<CarsUiState>(CarsUiState.Loading())
    val uiState = _uiState.asStateFlow()

    init {
        loadCars()
    }

     fun loadCars() {
        viewModelScope.launch {
            _uiState.value = CarsUiState.Loading("Loading cars...")
            try {
                _cars.value = repository.getCars()
                _uiState.value = CarsUiState.Success(_cars.value)
            } catch (e: Exception) {
                _uiState.value = CarsUiState.Error(e)
                _cars.value = emptyList()
            }
        }
    }
}