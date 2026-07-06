package com.olio.trainsembleapp.routes

import com.olio.trainsembleapp.stations.Station
import io.github.agrevster.pocketbaseKotlin.dsl.query.ExpandRecord
import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val number: Int,
    val type: String,
    var nick: String?,
    var departure: String,
    val arrival : String,
    val origin: String,
    val destination: String,
    val created: String,
    val updated: String,
) : ExpandRecord<Station>() {

}

@Serializable
data class RouteUiType (
    val number: String,
    val type: String,
    var nick: String?,
    var departure: String,
    val arrival : String,
    val origin: String,
    val destination: String,
)

fun Route.toUi() = RouteUiType(
    number = number.toString(),
    type = type,
    nick = nick,
    departure = departure,
    arrival = arrival,
    origin = origin,
    destination = destination,
)


@Serializable
data class RouteDto(
    val number: Int,
    val type: String,
    var nick: String?,
    var departure: String,
    val arrival : String,
    val origin: String,
    val destination: String,
)

fun RouteUiType.toDto(): RouteDto {
    return RouteDto(
        number = this.number.toInt(),
        type = this.type,
        nick = this.nick,
        departure = this.departure,
        arrival = this.arrival,
        origin = this.origin,
        destination = this.destination,
    )
}