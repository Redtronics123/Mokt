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

package dev.redtronics.mokt.core

import dev.redtronics.mokt.http.Http
import dev.redtronics.mokt.payloads.UserUUIDsPayload
import dev.redtronics.mokt.response.UserUUID
import dev.redtronics.mokt.response.UserUUIDNoContent
import dev.redtronics.mokt.response.UserUUIDNotFound
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.plugins.*

class Mokt {
    companion object {
        suspend fun getUserUUID(username: String): UserUUID {
            require(username.isNotBlank()) { "Username cannot be blank" }

            val response = Http.client.get {
                url(urlString = "https://api.mojang.com/users/profiles/minecraft/${username.lowercase()}")
            }

            if (response.status.value == 204) {
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
            return response.body<UserUUID>()
        }

        suspend fun getUserUUIDs(vararg usernames: String): MutableList<UserUUID> {
            require(usernames.isNotEmpty() && usernames.size > 10) { "Usernames cannot be empty and must be less than 10" }

            usernames.forEach { username ->
                username.lowercase()
            }

            val payload = UserUUIDsPayload(usernames.toList())

            val response = Http.client.post {
                contentType(ContentType.Application.Json)
                url(urlString = "https://api.minecraftservices.com/minecraft/profile/lookup/bulk/byname")
                setBody(payload)
            }

            if (!response.status.isSuccess()) {
                throw BadRequestException(
                    "Failed to get user UUIDs. Status: ${response.status.value} Message: ${response.status.description}"
                )
            }

            return response.body<MutableList<UserUUID>>()
        }

        suspend fun getUserUUIDs(usernames: List<String>): MutableList<UserUUID> {
            require(usernames.isNotEmpty() && usernames.size > 10) { "Usernames cannot be empty and must be less than 10" }

            usernames.forEach { username ->
                username.lowercase()
            }

            val payload = UserUUIDsPayload(usernames)

            val response = Http.client.post {
                contentType(ContentType.Application.Json)
                url(urlString = "https://api.minecraftservices.com/minecraft/profile/lookup/bulk/byname")
                setBody(payload)
            }

            if (!response.status.isSuccess()) {
                throw BadRequestException(
                    "Failed to get user UUIDs. Status: ${response.status.value} Message: ${response.status.description}"
                )
            }

            return response.body<MutableList<UserUUID>>()
        }
    }
}