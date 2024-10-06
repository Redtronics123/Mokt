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

@file:Suppress("MemberVisibilityCanBePrivate")

package dev.redtronics.mokt.builder

import dev.redtronics.mokt.payload.XBoxPayload
import dev.redtronics.mokt.payload.XBoxProperties
import dev.redtronics.mokt.provider.response.AccessResponse
import dev.redtronics.mokt.response.XBoxResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

public class XBoxBuilder internal constructor(
    override val httpClient: HttpClient,
    override val json: Json,
    private val accessResponse: AccessResponse
) : BaseBuilder() {
    public val relyingPartyUrl: Url
        get() = Url("http://auth.xboxlive.com")

    public val siteUrl: Url
        get() = Url("https://user.auth.xboxlive.com/user/authenticate")

    public val tokenType: TokenType = TokenType.JWT

    public val xAuthMethod: String
        get() = "RPS"

    public val rpsTicket: String
        get() = "d=${accessResponse.accessToken}"

    internal suspend fun build(onRequestError: suspend () -> Unit): XBoxResponse? {
        val payload = XBoxPayload(
            properties = XBoxProperties(
                xAuthMethod = xAuthMethod,
                siteName = siteUrl.host,
                rpsTicket = rpsTicket
            ),
            relyingParty = relyingPartyUrl.toString(),
            tokenType = tokenType
        )

        val response = httpClient.post {
            url(siteUrl)
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(XBoxPayload.serializer(), payload))
        }

        if (!response.status.isSuccess()) {
            onRequestError()
            return null
        }

        return json.decodeFromString(XBoxResponse.serializer(), response.bodyAsText())
    }
}

@Serializable(with = TokenType.Serializer::class)
public enum class TokenType(public val type: String) {
    JWT("JWT"),
    UNKNOWN("UNKNOWN");

    internal object Serializer : KSerializer<TokenType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(TokenType::class.simpleName!!, PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): TokenType = entries.find { it.type == decoder.decodeString() } ?: UNKNOWN

        override fun serialize(encoder: Encoder, value: TokenType) {
            encoder.encodeString(value.type)
        }
    }
}