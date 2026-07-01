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

     fun loadCars(reload: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = CarsUiState.Loading("Loading cars...")
            try {
                _cars.value = repository.getCars(reload)
                _uiState.value = CarsUiState.Success(_cars.value)
            } catch (e: Exception) {
                _uiState.value = CarsUiState.Error(e)
                _cars.value = emptyList()
            }
        }
    }
    fun deleteCar(id: String) {
        viewModelScope.launch {
            _uiState.value = CarsUiState.Loading("Deleting...")
            try {
                if (repository.deleteCarById(id)) {
                    loadCars()
                } else {
                    _uiState.value = CarsUiState.Error(Exception("Failed to delete"))
                }
            } catch (e: Exception) {
                _uiState.value = CarsUiState.Error(e)
            }
        }
    }
    fun editCar(id: String, entry: CarDto) {
        viewModelScope.launch {
            _uiState.value = CarsUiState.Loading("Saving...")
            try {
                val edited = repository.editCar(id, entry)
                loadCars(true)
            } catch (e: Exception) {
                _uiState.value = CarsUiState.Error(e)
            }
        }
    }
}