package com.olio.trainsembleapp.generic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olio.trainsembleapp.cars.Car
import com.olio.trainsembleapp.cars.CarDto
import com.olio.trainsembleapp.cars.CarsRepository
import com.olio.trainsembleapp.stations.Station
import com.olio.trainsembleapp.stations.StationDto
import com.olio.trainsembleapp.stations.StationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarsViewModel(repository: CarsRepository) : GenericViewModel<Car, CarDto>(repository)
class StationsViewModel(repository: StationsRepository) : GenericViewModel<Station, StationDto>(repository)

open class GenericViewModel<T, TDto>(
    private val repository: GenericRepository<T, TDto>
) : ViewModel() {

    private val _items = MutableStateFlow<List<T>>(emptyList())
    private val _uiState = MutableStateFlow<UiState<T>>(UiState.Loading())
    val uiState = _uiState.asStateFlow()

    init {
        loadItems()
    }

     fun loadItems(reload: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Loading items...")
            try {
                _items.value = repository.getItems(reload)
                _uiState.value = UiState.Success(_items.value)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e)
                _items.value = emptyList()
            }
        }
    }
    fun deleteItem(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Deleting...")
            try {
                if (repository.deleteItemById(id)) {
                    loadItems()
                } else {
                    _uiState.value = UiState.Error(Exception("Failed to delete"))
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e)
            }
        }
    }
    fun editItem(id: String, entry: TDto) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Saving...")
            try {
                val edited = repository.editItem(id, entry)
                loadItems(true)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e)
            }
        }
    }
    fun createItem(entry: TDto) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Saving...")
            try {
                val edited = repository.createItem(entry)
                loadItems(true)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e)
            }
        }
    }
}

