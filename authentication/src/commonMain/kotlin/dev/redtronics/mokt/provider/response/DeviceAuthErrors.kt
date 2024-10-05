/*
 * MIT License
 * Copyright 2024 Nils Jäkel
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software.
 */

package dev.redtronics.mokt.provider.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
public data class DeviceAuthStateError(
    val error: DeviceAuthStateErrorItem,
    @SerialName("error_description")
    val errorDescription: String
)

@Serializable(DeviceAuthStateErrorItem.Serializer::class)
public enum class DeviceAuthStateErrorItem(public val value: String) {
    AUTHORIZATION_PENDING("authorization_pending"),
    INVALID_REQUEST("invalid_request"),
    AUTHORIZATION_DECLINED("authorization_declined"),
    BAD_VERIFICATION_CODE("bad_verification_code"),
    EXPIRED_TOKEN("expired_token"),
    INVALID_SCOPE("invalid_scope");

    internal object Serializer : KSerializer<DeviceAuthStateErrorItem> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(DeviceAuthStateErrorItem::class.simpleName!!, PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): DeviceAuthStateErrorItem = byName(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: DeviceAuthStateErrorItem) = encoder.encodeString(value.value)
    }

    public companion object {
        public fun byName(name: String): DeviceAuthStateErrorItem = entries.firstOrNull { it.value == name } ?: INVALID_REQUEST
    }
}

@Serializable
public data class CodeErrorResponse(
    public val error: CodeError,
    @SerialName("error_description")
    public val errorDescription: String
)

@Serializable(with = CodeError.Serializer::class)
public enum class CodeError(public val value: String) {
    INVALID_REQUEST("invalid_request"),
    INVALID_CLIENT("invalid_client"),
    UNAUTHORIZED_CLIENT("unauthorized_client"),
    ACCESS_DENIED("access_denied"),
    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type"),
    SERVER_ERROR("server_error"),
    TEMPORARY_UNAVAILABLE("temporarily_unavailable"),
    INVALID_RESOURCE("invalid_resource"),
    LOGIN_REQUIRED("login_required"),
    INTERACTION_REQUIRED("interaction_required");

    internal object Serializer : KSerializer<CodeError> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(CodeError::class.simpleName!!, PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): CodeError = byName(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: CodeError) = encoder.encodeString(value.value)
    }

    public companion object {
        public fun byName(name: String): CodeError = entries.firstOrNull { it.value == name } ?: INVALID_REQUEST
    }
}