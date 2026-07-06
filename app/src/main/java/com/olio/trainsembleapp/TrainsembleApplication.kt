package com.olio.trainsembleapp

import android.app.Application
import com.olio.trainsembleapp.cars.CarsRepository
import com.olio.trainsembleapp.stations.StationsRepository
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import io.github.agrevster.pocketbaseKotlin.dsl.login
import io.github.agrevster.pocketbaseKotlin.models.AuthRecord
import io.ktor.http.URLProtocol
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TrainsembleApplication : Application() {
    lateinit var pbClient: PocketbaseClient private set
    lateinit var authRepository: AuthRepository private set
    lateinit var carsRepository: CarsRepository private set
    lateinit var stationsRepository: StationsRepository private set
    override fun onCreate() {
        super.onCreate()

        pbClient = PocketbaseClient({
            protocol = URLProtocol.HTTPS
            user= BuildConfig.PB_USER
            password = BuildConfig.PB_PASS
            host = "REDACTED_URL"
        })
        authRepository = AuthRepository(pbClient)
        carsRepository = CarsRepository(authRepository)
        stationsRepository = StationsRepository(authRepository)
        MainScope().launch {
            authRepository.authenticate()
        }


    }
}