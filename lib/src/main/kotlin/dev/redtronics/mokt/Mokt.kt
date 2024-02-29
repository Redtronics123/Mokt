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
import dev.redtronics.mokt.http.Http
import dev.redtronics.mokt.http.ResponseHandler
import dev.redtronics.mokt.types.UUID
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class Mokt(
    private val minecraftAuthToken: String? = null,
) {
    suspend fun getPlayerUUID(username: String): PlayerUUID {
        require(username.isNotBlank()) { "Username cannot be blank" }

        val response = Http.client.get {
            url(urlString = "${BuildConstants.MINECRAFT_API_URL}/users/profiles/minecraft/${username.lowercase()}")
        }

        ResponseHandler.validate(response)
        return response.body<PlayerUUID>()
    }

    suspend fun getPlayerUUIDs(vararg usernames: String): MutableList<PlayerUUID> {
        require(usernames.isNotEmpty() && usernames.size <= 10) { "Usernames must be between 1 and 10" }
        return getPlayerUUIDs(usernames.toMutableList())
    }

    suspend fun getPlayerUUIDs(usernames: MutableList<String>): MutableList<PlayerUUID> {
        require(value = usernames.isNotEmpty() && usernames.size <= 10) { "Usernames must be between 1 and 10" }

        usernames.forEach { username ->
            username.lowercase()
        }

        val payload = PlayerUUIDPayload(usernames = usernames)
        val response = Http.client.post {
            contentType(ContentType.Application.Json)
            url(urlString = "${BuildConstants.MINECRAFT_SERVICE_URL}/minecraft/profile/lookup/bulk/byname")
            setBody(payload)
        }

        ResponseHandler.validate(response)
        return response.body<MutableList<PlayerUUID>>()
    }

    suspend fun getPlayerName(uuid: UUID): PlayerName {
        val response = Http.client.get {
            url(urlString = "${BuildConstants.MINECRAFT_API_URL}/user/profile/${uuid.value}")
        }

        ResponseHandler.validate(response)
        return response.body<PlayerName>()
    }

    suspend fun getBlockedServer(limit: Int = 20): BlockedServer {
        require(value = limit >= 0) { "The limit cant low than 0" }

        val response = Http.client.get {
            url(urlString = "${BuildConstants.MINECRAFT_SESSION_URL}/blockedservers")
        }

        ResponseHandler.validate(response)
        val responseList = response.bodyAsText().trim().split("\n").toMutableList()

        if (limit == 0) {
            return BlockedServer(responseList)
        }

        return BlockedServer(
            responseList.take(limit).toMutableList()
        )
    }

    suspend fun getPlayerProfile(uuid: UUID, unsigned: Boolean = true): PlayerProfile {
        val response = Http.client.get {
            url(urlString = "${BuildConstants.MINECRAFT_SESSION_URL}/session/minecraft/profile/${uuid.value}?unsigned=$unsigned")
        }

        ResponseHandler.validate(response)
        return response.body<PlayerProfile>()
    }
}
