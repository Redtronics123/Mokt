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
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class AvailableUsername(
    val status: Status
)

@Serializable(with = Status.StatusSerializer::class)
enum class Status(val value: String) {
    AVAILABLE("AVAILABLE"),
    DUPLICATE("DUPLICATE"),
    NOT_ALLOWED("NOT_ALLOWED");

    internal object StatusSerializer : KSerializer<Status> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Status", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Status) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): Status {
            return entries.associateBy { it.value }[decoder.decodeString()] ?: throw IllegalArgumentException("Unknown Status")
        }
    }
}
