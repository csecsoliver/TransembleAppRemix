package com.olio.trainsembleapp.sightings

import com.olio.trainsembleapp.AuthRepository
import com.olio.trainsembleapp.AuthState
import com.olio.trainsembleapp.a_generic.GenericRepository
import com.olio.trainsembleapp.stations.Sighting
import com.olio.trainsembleapp.stations.SightingDto
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import kotlinx.serialization.json.Json

class SightingsRepository(private val authRepository: AuthRepository): GenericRepository<Sighting, SightingDto>{
    private val pbClient: PocketbaseClient = authRepository.pbClient
    private var cache: List<Sighting>? = null

    override suspend fun getItems(forceRefresh: Boolean): List<Sighting> {
        if (authRepository.isAuthenticated.value is AuthState.NotAuthenticated) {
            throw IllegalStateException("Not authenticated")
        }
        if (forceRefresh || cache == null) {
            cache = pbClient.records.getFullList<Sighting>("sightings", 1000)
        }
        return cache ?: emptyList()
    }

    override suspend fun getItemById(id: String): Sighting? {
        return getItems().find { it.id == id }
    }
    override suspend fun deleteItemById(id: String): Boolean {
        return pbClient.records.delete("sightings", id)
    }
    override suspend fun editItem(id: String, entry: SightingDto): Sighting {
        return pbClient.records.update<Sighting>("sightings", id, Json.encodeToString(entry))
    }
    override suspend fun createItem(entry: SightingDto): Sighting {
        return pbClient.records.create<Sighting>("sightings", Json.encodeToString(entry))
    }
}