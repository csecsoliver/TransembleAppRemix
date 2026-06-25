package com.olio.trainsembleapp

import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import io.github.agrevster.pocketbaseKotlin.models.utils.ListResult

class CarsRepository(private val pbClient: PocketbaseClient) {

    private var cachedCars: List<Car>? = null

    suspend fun getCars(forceRefresh: Boolean = false): List<Car> {
        if (forceRefresh || cachedCars == null) {

                cachedCars = pbClient.records.getFullList<Car>("cars", 10)
        }
        return cachedCars ?: emptyList()
    }

    suspend fun getCarById(id: String): Car? {
        return getCars().find { it.id == id }
    }
}