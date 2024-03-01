package dev.redtronics.mokt.entity

import dev.redtronics.mokt.types.UUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerName(
    val name: String,
    @SerialName("id")
    val uuid: UUID,
)
