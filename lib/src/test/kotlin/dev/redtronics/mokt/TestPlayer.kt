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

import dev.redtronics.mokt.types.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class TestPlayer {
    private val mokt = Mokt()

    @Test
    fun `test to get player name`() = runBlocking {
        val uuid = "430d7fb21add41b5b9217995f8cac3e7"
        val playerNameData = mokt.getPlayerName(UUID(value = "430d7fb21add41b5b9217995f8cac3e7"))
        assert(playerNameData.name == "Redtronics123" && playerNameData.uuid == uuid)
    }

    @Test
    fun `test to get player uuid`() = runBlocking {
        val playerName = "Redtronics123"
        val playerUUIDData = mokt.getPlayerUUID(username = playerName)
        assert(playerUUIDData.name == playerName && playerUUIDData.uuid.value == "430d7fb21add41b5b9217995f8cac3e7")
    }

    @Test
    fun `test to get player uuids with vararg`() = runBlocking {
        val playerUUIDsData = mokt.getPlayerUUIDs("Redtronics123", "Notch")
        if (playerUUIDsData.size != 2) {
            throw Exception("Player UUIDs size is not 2")
        }

        when(playerUUIDsData[0].name) {
            "Redtronics123" -> assert(playerUUIDsData[0].uuid.value == "430d7fb21add41b5b9217995f8cac3e7" && playerUUIDsData[1].name == "Notch" && playerUUIDsData[1].uuid.value == "069a79f444e94726a5befca90e38aaf5")
            "Notch" -> assert(playerUUIDsData[0].uuid.value == "069a79f444e94726a5befca90e38aaf5" && playerUUIDsData[1].name == "Redtronics123" && playerUUIDsData[1].uuid.value == "430d7fb21add41b5b9217995f8cac3e7")
            else -> throw Exception("Player UUIDs are not correct")
        }
    }

    @Test
    fun `test to get player uuids with list`() = runBlocking {
        val playerUUIDsData = mokt.getPlayerUUIDs(mutableListOf("Redtronics123", "Notch"))
        if (playerUUIDsData.size != 2) {
            throw Exception("Player UUIDs size is not 2")
        }

        when(playerUUIDsData[0].name) {
            "Redtronics123" -> assert(playerUUIDsData[0].uuid.value == "430d7fb21add41b5b9217995f8cac3e7" && playerUUIDsData[1].name == "Notch" && playerUUIDsData[1].uuid.value == "069a79f444e94726a5befca90e38aaf5")
            "Notch" -> assert(playerUUIDsData[0].uuid.value == "069a79f444e94726a5befca90e38aaf5" && playerUUIDsData[1].name == "Redtronics123" && playerUUIDsData[1].uuid.value == "430d7fb21add41b5b9217995f8cac3e7")
            else -> throw Exception("Player UUIDs are not correct")
        }
    }
}