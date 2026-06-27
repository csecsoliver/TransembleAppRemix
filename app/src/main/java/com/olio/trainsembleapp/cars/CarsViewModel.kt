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
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    init {
        loadCars()
    }

    private fun loadCars() {
        viewModelScope.launch {
                _cars.value = repository.getCars()
        }
    }
}