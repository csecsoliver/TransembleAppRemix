package com.olio.trainsembleapp

import io.github.agrevster.pocketbaseKotlin.models.Record
import io.github.agrevster.pocketbaseKotlin.models.utils.BaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Car(
    @SerialName("UIC")
    val uic: String,
    val agency: String, // e.g., Locomotive, Freight, Passenger, Tanker
    val type: String,
    val nick: String?,
    val vagonweb: String,
    val created: String,
    val updated: String,
) : Record()