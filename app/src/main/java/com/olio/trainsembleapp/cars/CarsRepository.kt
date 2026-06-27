package com.olio.trainsembleapp.cars

import com.olio.trainsembleapp.AuthRepository
import com.olio.trainsembleapp.AuthState
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient

class CarsRepository(private val authRepository: AuthRepository){
    private val pbClient: PocketbaseClient = authRepository.pbClient
    private var cachedCars: List<Car>? = null

    suspend fun getCars(forceRefresh: Boolean = false): List<Car> {
        if (authRepository.isAuthenticated.value is AuthState.NotAuthenticated) {
            throw IllegalStateException("Not authenticated")
        }
        if (forceRefresh || cachedCars == null) {
            cachedCars = pbClient.records.getFullList<Car>("cars", 10)
        }
        return cachedCars ?: emptyList()
    }

    suspend fun getCarById(id: String): Car? {
        return getCars().find { it.id == id }
    }
}