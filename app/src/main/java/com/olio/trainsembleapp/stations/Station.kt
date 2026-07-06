package com.olio.trainsembleapp.stations

import io.github.agrevster.pocketbaseKotlin.models.Record
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Station(
    val name: String,
    val created: String,
    val updated: String,
) : Record() {

}

@Serializable
data class StationDto (
    val name: String,
)

fun Station.toDto() = StationDto(
    name = name
)