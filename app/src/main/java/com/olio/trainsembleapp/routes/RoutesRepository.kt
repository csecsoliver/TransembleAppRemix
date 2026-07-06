package com.olio.trainsembleapp.routes

import com.olio.trainsembleapp.AuthRepository
import com.olio.trainsembleapp.AuthState
import com.olio.trainsembleapp.a_generic.GenericRepository
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import io.github.agrevster.pocketbaseKotlin.dsl.query.ExpandRelations
import kotlinx.serialization.json.Json

class RoutesRepository(private val authRepository: AuthRepository): GenericRepository<Route, RouteUiType>{
    private val pbClient: PocketbaseClient = authRepository.pbClient
    private var cache: List<Route>? = null

    override suspend fun getItems(forceRefresh: Boolean): List<Route> {
        if (authRepository.isAuthenticated.value is AuthState.NotAuthenticated) {
            throw IllegalStateException("Not authenticated")
        }
        if (forceRefresh || cache == null) {
            cache = pbClient.records.getFullList<Route>("routes", 1000, expandRelations = ExpandRelations("origin,destination"))
        }
        return cache ?: emptyList()
    }

    override suspend fun getItemById(id: String): Route? {
        return getItems().find { it.id == id }
    }
    override suspend fun deleteItemById(id: String): Boolean {
        return pbClient.records.delete("routes", id)
    }
    override suspend fun editItem(id: String, entry: RouteUiType): Route {
        val dto = entry.toDto()
        return pbClient.records.update<Route>("routes", id, Json.encodeToString(dto))
    }
    override suspend fun createItem(entry: RouteUiType): Route {
        return pbClient.records.create<Route>("routes", Json.encodeToString(entry))
    }
}