package dev.redtronics.mokt.entity

import kotlinx.serialization.Serializable

@Serializable
data class BlockedServer(
    val hashedAddresses: MutableList<String>
)
