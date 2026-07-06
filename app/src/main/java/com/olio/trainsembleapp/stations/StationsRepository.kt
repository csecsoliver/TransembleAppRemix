package com.olio.trainsembleapp.stations

import com.olio.trainsembleapp.AuthRepository
import com.olio.trainsembleapp.AuthState
import com.olio.trainsembleapp.generic.GenericRepository
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import kotlinx.serialization.json.Json

class StationsRepository(private val authRepository: AuthRepository): GenericRepository<Station, StationDto>{
    private val pbClient: PocketbaseClient = authRepository.pbClient
    private var cache: List<Station>? = null

    override suspend fun getItems(forceRefresh: Boolean): List<Station> {
        if (authRepository.isAuthenticated.value is AuthState.NotAuthenticated) {
            throw IllegalStateException("Not authenticated")
        }
        if (forceRefresh || cache == null) {
            cache = pbClient.records.getFullList<Station>("stations", 10)
        }
        return cache ?: emptyList()
    }

    override suspend fun getItemById(id: String): Station? {
        return getItems().find { it.id == id }
    }
    override suspend fun deleteItemById(id: String): Boolean {
        return pbClient.records.delete("stations", id)
    }
    override suspend fun editItem(id: String, entry: StationDto): Station {
        return pbClient.records.update<Station>("stations", id, Json.encodeToString(entry))
    }
    override suspend fun createItem(entry: StationDto): Station {
        return pbClient.records.create<Station>("stations", Json.encodeToString(entry))
    }
}