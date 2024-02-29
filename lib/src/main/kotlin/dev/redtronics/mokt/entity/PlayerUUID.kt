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

package dev.redtronics.mokt.entity

import dev.redtronics.mokt.BuildConstants
import dev.redtronics.mokt.http.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

internal class PlayerUUID(
    private val username: String,
) {
    suspend fun getPlayerUUID(): PlayerUUIDData {
        require(username.isNotBlank()) { "Username cannot be blank" }

        val response = Http.client.get {
            url(urlString = "${BuildConstants.MINECRAFT_API_URL}/users/profiles/minecraft/${username.lowercase()}")
        }

        ResponseHandler.validate(response)
        return response.body<PlayerUUIDData>()
    }

    companion object {
        suspend fun getPlayerUUIDs(vararg usernames: String): MutableList<PlayerUUIDData> {
            require(usernames.isNotEmpty() && usernames.size <= 10) { "Usernames must be between 1 and 10"}
            return getPlayerUUIDs(usernames.toMutableList())
        }

        suspend fun getPlayerUUIDs(usernames: MutableList<String>): MutableList<PlayerUUIDData> {
            require(usernames.isNotEmpty() && usernames.size <= 10) { "Usernames must be between 1 and 10"}

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
            return response.body<MutableList<PlayerUUIDData>>()
        }
    }
}


@Serializable
data class PlayerUUIDData(
    val name: String,
    val id: String,
)

@Serializable
data class PlayerUUIDPayload(
    val usernames: List<String>,
)