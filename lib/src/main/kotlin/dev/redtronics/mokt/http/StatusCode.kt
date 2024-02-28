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

package dev.redtronics.mokt.http

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = StatusCode.StatusCodeSerializer::class)
enum class StatusCode(val code: Int) {
    NOT_FOUND(404);

    internal object StatusCodeSerializer : KSerializer<StatusCode> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StatusCode", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: StatusCode) {
            encoder.encodeInt(value.code)
        }

        override fun deserialize(decoder: Decoder): StatusCode {
            return entries.associateBy { it.code }[decoder.decodeInt()] ?: throw IllegalArgumentException("Invalid status code")
        }
    }
}
