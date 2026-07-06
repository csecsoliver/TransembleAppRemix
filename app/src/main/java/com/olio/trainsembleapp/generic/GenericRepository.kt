package com.olio.trainsembleapp.generic

interface GenericRepository<T, TDto> {
    suspend fun getItems(forceRefresh: Boolean = false): List<T>
    suspend fun getItemById(id: String): T?
    suspend fun deleteItemById(id: String): Boolean
    suspend fun editItem(id: String, entry: TDto): T
    suspend fun createItem(entry: TDto): T
}