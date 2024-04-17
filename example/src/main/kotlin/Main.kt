package dev.redtronics

import dev.redtronics.mokt.auth.MSAuth
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.supervisorScope

val httpClient: HttpClient = HttpClient(CIO) {
    install(Logging) {
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json()
    }
}

suspend fun main(): Unit = supervisorScope {
    val msAuth = MSAuth.OAuth(
        clientId = "1e8287cd-e119-4829-877c-10a13996dedd",
        httpClient = httpClient
    )
}
