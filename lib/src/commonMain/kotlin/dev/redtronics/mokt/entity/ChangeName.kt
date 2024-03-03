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
import kotlinx.uuid.UUID

@Serializable
data class ChangedName(
    val uuid: String,
    val name: String,
    val skins: List<Skin>
)

@Serializable
data class Skin(
    @SerialName("id")
    val uuid: UUID,
    val state: String,
    val url: String,
    val variant: Variant
)

@Serializable(with = Variant.VariantSerializer::class)
enum class Variant(val value: String) {
    SLIM("SLIM"),
    DEFAULT("CLASSIC");

    internal object VariantSerializer : KSerializer<Variant> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Variant", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Variant) = encoder.encodeString(value.value)

        override fun deserialize(decoder: Decoder): Variant {
            return entries.associateBy { it.value }[decoder.decodeString()] ?: throw IllegalArgumentException("Unknown Variant")
        }
    }
}
