package dev.redtronics.mokt.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerName(
    val name: String,
    @SerialName("id")
    val uuid: String,
)
