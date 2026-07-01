package com.olio.trainsembleapp.cars

import io.github.agrevster.pocketbaseKotlin.models.Record
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Car(
    @SerialName("UIC")
    val uic: String,
    val agency: String,
    val type: String,
    val nick: String?,
    val vagonweb: String,
    val created: String,
    val updated: String,
) : Record() {

}

@Serializable
data class CarDto (
    @SerialName("UIC")
    val uic: String,
    val agency: String,
    val type: String,
    val nick: String?,
    val vagonweb: String,
)