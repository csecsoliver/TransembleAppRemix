package com.olio.trainsembleapp

import android.app.Application
import com.olio.trainsembleapp.cars.CarsRepository
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import io.github.agrevster.pocketbaseKotlin.dsl.login
import io.github.agrevster.pocketbaseKotlin.models.AuthRecord
import io.ktor.http.URLProtocol
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TrainsembleApplication : Application() {
    lateinit var pbClient: PocketbaseClient private set
    lateinit var carsRepository: CarsRepository private set
    override fun onCreate() {
        super.onCreate()

        pbClient = PocketbaseClient({
            protocol = URLProtocol.HTTPS
            user= BuildConfig.PB_USER
            password = BuildConfig.PB_PASS
            host = "REDACTED_URL"
        })
        MainScope().launch {
            val tokent = pbClient.records.authWithPassword<AuthRecord>(
                "users", "REDACTED_EMAIL", "REDACTED_PASSWORD"
            ).token
            pbClient.login { token = tokent }
            carsRepository = CarsRepository(pbClient)
        }


    }
}