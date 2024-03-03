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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


interface Privileg {
    val enabled: Boolean
}

@Serializable
data class PlayerAttributes(
    val privileges: Privileges,
    val profanityFilterPreferences: ProfanityFilterPreferences,
    val banStatus: BanStatus
)

@Serializable
data class Privileges(
    val onlineChat: OnlineChat,
    val multiplayerServer: MultiplayerServer,
    val multiplayerRealms: MultiplayerRealms,
    val telemetry: Telemetry
)

@Serializable
data class OnlineChat(override val enabled: Boolean) : Privileg

@Serializable
data class MultiplayerServer(override val enabled: Boolean) : Privileg

@Serializable
data class MultiplayerRealms(override val enabled: Boolean) : Privileg

@Serializable
data class Telemetry(override val enabled: Boolean): Privileg

@Serializable
data class ProfanityFilterPreferences(
    val profanityFilterOn: Boolean
)

@Serializable
data class BanStatus(
    val bannedScopes: BanScopes
)

@Serializable
data class BanScopes(
    @SerialName("MULTIPLAYER")
    val multiplayer: Multiplayer? = null
)

@Serializable
data class Multiplayer(
    val banId: String,
    val expires: Long? = null,
    val reason: BanReasons,
    val reasonMessage: String? = null
)

@Serializable(with = BanReasons.BanReasonsSerializer::class)
enum class BanReasons(val reason: String) {
    FALSE_REPORT("false_reporting"),
    HATE_SPEECH("hate_speech"),
    TERRORISM("terrorism_or_violent_extremism"),
    CHILD_SEXUAL_EXPLOITATION("child_sexual_exploitation_or_abuse"),
    IMMINENT_HARM("imminent_harm"),
    NON_CONSENSUAL_INTIMATE_IMAGERY("non_consensual_intimate_imagery"),
    HARASSMENT_OR_BULLYING("harassment_or_bullying"),
    DEFAMATION_IMPERSONATION_FALSE_INFORMATION("defamation_impersonation_false_information"),
    SELF_HARM_OR_SUICIDE("self_harm_or_suicide"),
    ALCOHOL_TABACCO_DRUGS("alcohol_tobacco_drugs");

    internal object BanReasonsSerializer : KSerializer<BanReasons> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BanReasons", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): BanReasons {
            return entries.associateBy { it.reason }[decoder.decodeString()] ?: throw IllegalArgumentException()
        }

        override fun serialize(encoder: Encoder, value: BanReasons) {
            encoder.encodeString(value.reason)
        }
    }
}
