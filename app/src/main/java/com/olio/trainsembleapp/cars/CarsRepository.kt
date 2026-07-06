package com.olio.trainsembleapp.cars

import com.olio.trainsembleapp.AuthRepository
import com.olio.trainsembleapp.AuthState
import com.olio.trainsembleapp.a_generic.GenericRepository
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import kotlinx.serialization.json.Json

class CarsRepository(private val authRepository: AuthRepository): GenericRepository<Car, CarDto>{
    private val pbClient: PocketbaseClient = authRepository.pbClient
    private var cachedCars: List<Car>? = null

    override suspend fun getItems(forceRefresh: Boolean): List<Car> {
        if (authRepository.isAuthenticated.value is AuthState.NotAuthenticated) {
            throw IllegalStateException("Not authenticated")
        }
        if (forceRefresh || cachedCars == null) {
            cachedCars = pbClient.records.getFullList<Car>("cars", 1000)
        }
        return cachedCars ?: emptyList()
    }

    override suspend fun getItemById(id: String): Car? {
        return getItems().find { it.id == id }
    }
    override suspend fun deleteItemById(id: String): Boolean {
        return pbClient.records.delete("cars", id)
    }
    override suspend fun editItem(id: String, entry: CarDto): Car {
        return pbClient.records.update<Car>("cars", id, Json.encodeToString(entry))
    }
    override suspend fun createItem(entry: CarDto): Car {
        return pbClient.records.create<Car>("cars", Json.encodeToString(entry))
    }
}