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

import dev.redtronics.mokt.build.BuildConstants
import dev.redtronics.mokt.http.Http
import dev.redtronics.mokt.http.StatusCode
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

class PlayerUUID(
    private val username: String
) {
    companion object {
        suspend fun getUserUUID(username: String): UserUUIDData {
            require(username.isNotBlank()) { "Username cannot be blank" }

            val response = Http.client.get {
                url(urlString = "${BuildConstants.minecraftApiUrl}/users/profiles/minecraft/${username.lowercase()}")
            }

            if (response.status.value == StatusCode.NOT_FOUND.code) {
                val errorResponse = response.body<UserUUIDNoContent>()
                throw IllegalArgumentException(
                    "User with username $username does not exist. Error: ${errorResponse.errorMessage} Path: ${errorResponse.path}"
                )
            }

            if (response.status.value == 404) {
                val errorResponse = response.body<UserUUIDNotFound>()
                throw IllegalArgumentException(
                    "User with username $username does not exist. Error: ${errorResponse.error} Message: ${errorResponse.errorMessage}"
                )
            }
            return response.body<UserUUIDData>()
        }
    }
}

@Serializable
data class PlayerUUIDPayload(
    val usernames: List<String>
)

@Serializable
data class UserUUIDData(
    val name: String,
    val id: String
)

@Serializable
data class UserUUIDNoContent(
    val path: String,
    val errorMessage: String
)

@Serializable
data class UserUUIDNotFound(
    val error: String,
    val errorMessage: String
)