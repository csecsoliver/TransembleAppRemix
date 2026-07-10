package com.olio.trainsembleapp.stations

import android.media.Image
import android.net.Uri
import io.github.agrevster.pocketbaseKotlin.models.Record
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaLocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date
import kotlin.time.toJavaInstant

@Serializable
data class Sighting(

    val route: String,
    val date: String,
    val station: String,
    val cars: List<String>,
    val notes: String,
    val image: List<String>,
    val created: String,
    val updated: String,
) : Record() {

}

@Serializable
data class SightingDto (
    val route: String,
    val date: Long?,
    val station: String,
    val cars: List<String>,
    val notes: String
)

fun Sighting.toDto() = SightingDto(
    route = route,
    date = LocalDate.parse("2026-06-07").atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
    station = station,
    cars = cars,
    notes = notes,
)