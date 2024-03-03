/*
 * MIT License
 * Copyright 2024 Redtronics
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package dev.redtronics.mokt

import dev.redtronics.mokt.entity.*
import dev.redtronics.mokt.http.requireSuccessful
import dev.redtronics.mokt.types.UUID
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Mokt(
    private val httpClient: HttpClient,
    private val json: Json,
) {
    suspend fun getPlayerUUID(username: String): PlayerUUID {
        require(username.isNotBlank()) { "Username cannot be blank" }

        val response = httpClient.get {
            url(urlString = "${BuildConstants.MINECRAFT_API_URL}/users/profiles/minecraft/${username.lowercase()}")
        }
        
        response.requireSuccessful()
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getPlayerUUIDs(vararg usernames: String): List<PlayerUUID> {
        require(usernames.isNotEmpty() && usernames.size <= 10) { "Usernames must be between 1 and 10" }
        return getPlayerUUIDs(usernames.toMutableList())
    }

    suspend fun getPlayerUUIDs(usernames: List<String>): List<PlayerUUID> {
        require(value = usernames.isNotEmpty() && usernames.size <= 10) { "Usernames must be between 1 and 10" }

        usernames.forEach { username ->
            username.lowercase()
        }

        val payload = PlayerUUIDPayload(usernames = usernames)
        val response = httpClient.post {
            contentType(ContentType.Application.Json)
            url(urlString = "${BuildConstants.MINECRAFT_SERVICE_URL}/minecraft/profile/lookup/bulk/byname")
            setBody(json.encodeToString(payload.usernames))
        }

        response.requireSuccessful()
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getPlayerName(uuid: UUID): PlayerName {
        val response = httpClient.get {
            url(urlString = "${BuildConstants.MINECRAFT_API_URL}/user/profile/${uuid.value}")
        }

        response.requireSuccessful()
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getBlockedServer(limit: Int = 20): BlockedServer {
        require(value = limit >= 0) { "The limit cant low than 0" }

        val response = httpClient.get {
            url(urlString = "${BuildConstants.MINECRAFT_SESSION_URL}/blockedservers")
        }

        response.requireSuccessful()
        val responseList = response.bodyAsText().trim().split("\n").toMutableList()

        if (limit == 0) {
            return BlockedServer(responseList)
        }

        return BlockedServer(
            responseList.take(limit).toMutableList()
        )
    }

    suspend fun getPlayerProfile(uuid: UUID, unsigned: Boolean = true): PlayerProfile {
        val response = httpClient.get {
            url(urlString = "${BuildConstants.MINECRAFT_SESSION_URL}/session/minecraft/profile/${uuid.value}?unsigned=$unsigned")
        }

        response.requireSuccessful()
        return json.decodeFromString(response.bodyAsText())
    }

    class Public(
        httpClient: HttpClient,
        json: Json = Json {
            ignoreUnknownKeys = true
        }
    ) : Mokt(httpClient, json)

    class Authenticated(
        private val authToken: String,
        private val httpClient: HttpClient,
        private val json: Json = Json {
            ignoreUnknownKeys = true
        }
    ) : Mokt(httpClient, json) {
        private fun HttpRequestBuilder.authHeader() = headers { bearerAuth(token = authToken) }

        suspend fun checkNameAvailability(username: String): AvailableUsername {
            val response = httpClient.get {
                authHeader()
                url(urlString = "${BuildConstants.MINECRAFT_SERVICE_URL}/minecraft/profile/name/$username/available")
            }

            response.requireSuccessful()
            return json.decodeFromString(response.bodyAsText())
        }
    }
}
