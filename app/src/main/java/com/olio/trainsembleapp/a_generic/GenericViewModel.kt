package com.olio.trainsembleapp.a_generic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olio.trainsembleapp.cars.Car
import com.olio.trainsembleapp.cars.CarDto
import com.olio.trainsembleapp.cars.CarsRepository
import com.olio.trainsembleapp.routes.Route
import com.olio.trainsembleapp.routes.RouteUiType
import com.olio.trainsembleapp.routes.RoutesRepository
import com.olio.trainsembleapp.sightings.SightingsRepository
import com.olio.trainsembleapp.stations.Sighting
import com.olio.trainsembleapp.stations.SightingDto
import com.olio.trainsembleapp.stations.Station
import com.olio.trainsembleapp.stations.StationDto
import com.olio.trainsembleapp.stations.StationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class CarsViewModel(repository: CarsRepository) : GenericViewModel<Car, CarDto>(repository)
class StationsViewModel(repository: StationsRepository) : GenericViewModel<Station, StationDto>(repository)
class RoutesViewModel(repository: RoutesRepository,  private val stationsRepository: StationsRepository) : GenericViewModel<Route, RouteUiType>(repository){
    protected val _stationItems = MutableStateFlow<List<Station>>(emptyList())

    protected val _StationsUiState = MutableStateFlow<UiState<Station>>(UiState.Loading())
    val stationsUiState = _StationsUiState.asStateFlow()

    override fun loadItems(reload: Boolean) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Loading items...")
            try {
                _items.value = repository.getItems(reload)
                _stationItems.value = stationsRepository.getItems(reload)
                _uiState.value = UiState.Success(_items.value)
                _StationsUiState.value = UiState.Success(_stationItems.value)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e)
                _items.value = emptyList()
                _stationItems.value = emptyList()
            }
        }
    }
}


class SightingsViewModel(repository: SightingsRepository, private val routesRepository: RoutesRepository,  private val stationsRepository: StationsRepository, private val carsRepository: CarsRepository) : GenericViewModel<Sighting, SightingDto>(repository){
    protected val _stationItems = MutableStateFlow<List<Station>>(emptyList())
    protected val _StationsUiState = MutableStateFlow<UiState<Station>>(UiState.Loading())
    val stationsUiState = _StationsUiState.asStateFlow()

    protected val _routesItems = MutableStateFlow<List<Route>>(emptyList())
    protected val _RoutesUiState = MutableStateFlow<UiState<Route>>(UiState.Loading())
    val routesUiState = _RoutesUiState.asStateFlow()


    protected val _carsItems = MutableStateFlow<List<Car>>(emptyList())
    protected val _CarsUiState = MutableStateFlow<UiState<Car>>(UiState.Loading())
    val carsUiState = _CarsUiState.asStateFlow()


    override fun loadItems(reload: Boolean) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Loading items...")
            try {
                _items.value = repository.getItems(reload)
                _stationItems.value = stationsRepository.getItems(reload)
                _routesItems.value = routesRepository.getItems(reload)
                _carsItems.value = carsRepository.getItems(reload)

                _uiState.value = UiState.Success(_items.value)
                _StationsUiState.value = UiState.Success(_stationItems.value)
                _RoutesUiState.value = UiState.Success(_routesItems.value)
                _CarsUiState.value = UiState.Success(_carsItems.value)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e)
                _items.value = emptyList()
                _stationItems.value = emptyList()
                _routesItems.value = emptyList()
            }
        }
    }
}


open class GenericViewModel<T, TDto>(
    protected val repository: GenericRepository<T, TDto>
) : ViewModel() {

    protected val _items = MutableStateFlow<List<T>>(emptyList())
    protected val _uiState = MutableStateFlow<UiState<T>>(UiState.Loading())
    val uiState = _uiState.asStateFlow()

    private val _editState = MutableStateFlow<EditOperationState>(EditOperationState.Idle)
    val editState = _editState.asStateFlow()

    init {
        loadItems()
    }

     open fun loadItems(reload: Boolean = false) {
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
            _editState.value = EditOperationState.Loading("Deleting...")
            try {
                if (repository.deleteItemById(id)) {
                    _editState.value = EditOperationState.Success
                    loadItems()
                } else {
                    _editState.value = EditOperationState.Error(Exception("Failed to delete"))
                }
            } catch (e: Exception) {
                _editState.value = EditOperationState.Error(e)
            }
        }
    }
    fun editItem(id: String, entry: TDto) {
        viewModelScope.launch {
            _editState.value = EditOperationState.Loading("Saving...")
            try {
                repository.editItem(id, entry)
                _editState.value = EditOperationState.Success
                loadItems(true)
            } catch (e: Exception) {
                _editState.value = EditOperationState.Error(e)
            }
        }
    }
    fun createItem(entry: TDto) {
        viewModelScope.launch {
            _editState.value = EditOperationState.Loading("Saving...")
            try {
                repository.createItem(entry)
                _editState.value = EditOperationState.Success
                loadItems(true)
            } catch (e: Exception) {
                _editState.value = EditOperationState.Error(e)
            }
        }
    }

    fun clearEditError() {
        _editState.value = EditOperationState.Idle
    }
}

