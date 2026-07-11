package com.olio.trainsembleapp

import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import io.github.agrevster.pocketbaseKotlin.PocketbaseException
import io.github.agrevster.pocketbaseKotlin.dsl.login
import io.github.agrevster.pocketbaseKotlin.models.AuthRecord
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed class  AuthState {
    object NotAuthenticated : AuthState()
    object Authenticating : AuthState()
    data class Authenticated(val authRecord: AuthRecord) : AuthState()
    data class Error(val reason: String) : AuthState()
}

class AuthRepository(var pbClient: PocketbaseClient) {
    val isAuthenticated: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Authenticating)
    suspend fun authenticate() {
        try {
            val authToken = pbClient.records.authWithPassword<AuthRecord>(
                "users", BuildConfig.PB_USER, BuildConfig.PB_PASS
            ).token
            pbClient.login { token = authToken }
            isAuthenticated.value = AuthState.Authenticated(pbClient.records.refresh<AuthRecord>("users").record)
        } catch (e: Exception) {
            isAuthenticated.value = AuthState.Error(e.message ?: "Authentication failed")
        }
    }

    fun retry() {
        isAuthenticated.value = AuthState.Authenticating
        MainScope().launch { authenticate() }
    }

    suspend fun checkAuth(): String? {
        try {
            pbClient.records.refresh<AuthRecord>("users")
            return null
        } catch (e: PocketbaseException) {
            return e.reason
        }
    }

}