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

import dev.redtronics.mokt.entity.PlayerName
import dev.redtronics.mokt.entity.PlayerProfile
import dev.redtronics.mokt.entity.PlayerProfileProperties
import dev.redtronics.mokt.entity.PlayerUUID
import dev.redtronics.mokt.types.UUID
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TestPlayer : FunSpec({
    val playerName1 = "playerName1"
    val playerUuid1 = UUID("11111111111111111111111111111111")
    val playerName2 = "playerName2"
    val playerUuid2 = UUID("00000000000000000000000000000000")

    test("test to get player name") {
        val mockHttpClient = HttpClient(MockEngine {
            respond(Json.encodeToString(PlayerName(playerName1, playerUuid1)))
        })
        val mokt = Mokt.Public(mockHttpClient)
        val playerNameData = mokt.getPlayerName(playerUuid1)
        playerNameData.name shouldBe playerName1
        playerNameData.uuid shouldBe playerUuid1
    }

    test("test to get player uuid") {
        val mockHttpClient = HttpClient(MockEngine {
            respond(Json.encodeToString(PlayerUUID(playerUuid1, playerName1)))
        })
        val mokt = Mokt.Public(mockHttpClient)
        val playerUUIDData = mokt.getPlayerUUID(username = playerName1)
        playerUUIDData.name shouldBe playerName1
        playerUUIDData.uuid shouldBe playerUuid1
    }

    test("test to get player uuids with vararg") {
        val mockHttpClient = HttpClient(MockEngine {
            respond(
                Json.encodeToString(
                    listOf(
                        PlayerUUID(playerUuid1, playerName1),
                        PlayerUUID(playerUuid2, playerName2)
                    )
                )
            )
        })
        val mokt = Mokt.Public(mockHttpClient)
        val playerUUIDsData = mokt.getPlayerUUIDs(playerName1, playerName2)
        playerUUIDsData.shouldContainExactlyInAnyOrder(
            PlayerUUID(playerUuid1, playerName1),
            PlayerUUID(playerUuid2, playerName2)
        )
    }

    test("test to get player uuids with list") {
        val mockHttpClient = HttpClient(MockEngine {
            respond(
                Json.encodeToString(
                    listOf(
                        PlayerUUID(playerUuid1, playerName1),
                        PlayerUUID(playerUuid2, playerName2)
                    )
                )
            )
        })
        val mokt = Mokt.Public(mockHttpClient)
        val playerUUIDsData = mokt.getPlayerUUIDs(mutableListOf(playerName1, playerName2))
        playerUUIDsData.shouldContainExactlyInAnyOrder(
            PlayerUUID(playerUuid1, playerName1),
            PlayerUUID(playerUuid2, playerName2)
        )
    }

    test("test to get player profile without unsigned") {
        val playerDataProperties =
            PlayerProfileProperties(name = "textures", value = "dGVzdAo=", signature = "signature")
        val mockHttpClient = HttpClient(MockEngine {
            it.url.parameters["unsigned"] shouldBe "false"
            respond(Json.encodeToString(PlayerProfile(playerUuid1, playerName1, listOf(playerDataProperties), false)))
        })
        val mokt = Mokt.Public(mockHttpClient)
        val playerProfileData = mokt.getPlayerProfile(playerUuid1, false)
        playerProfileData.name shouldBe playerName1
        playerProfileData.uuid shouldBe playerUuid1
        playerProfileData.legacy shouldBe false
        playerProfileData.properties.shouldContainExactly(playerDataProperties)
    }

    test("test to get player profile with unsigned") {
        val playerDataProperties = PlayerProfileProperties(name = "textures", value = "dGVzdAo=", signature = null)
        val mockHttpClient = HttpClient(MockEngine {
            it.url.parameters["unsigned"] shouldBe "true"
            respond(Json.encodeToString(PlayerProfile(playerUuid1, playerName1, listOf(playerDataProperties), false)))
        })
        val mokt = Mokt.Public(mockHttpClient)
        val playerProfileData = mokt.getPlayerProfile(playerUuid1, true)
        playerProfileData.name shouldBe playerName1
        playerProfileData.uuid shouldBe playerUuid1
        playerProfileData.legacy shouldBe false
        playerProfileData.properties.shouldContainExactly(playerDataProperties)
    }
})
