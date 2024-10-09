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

import dev.redtronics.mokt.payload.XstsPayload
import dev.redtronics.mokt.payload.XstsProperties
import dev.redtronics.mokt.response.XBoxResponse
import dev.redtronics.mokt.response.XstsResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

public class XstsBuilder internal constructor(
    override val httpClient: HttpClient,
    override val json: Json,
    private val xBoxResponse: XBoxResponse
) : BaseBuilder() {
    public var sandboxId: String = "RETAIL"

    public var relyingParty: String = "rp://api.minecraftservices.com"

    public val authUrl: Url
        get() = Url("https://xsts.auth.xboxlive.com/xsts/authorize")

    internal suspend fun build(onRequestError: suspend () -> Unit): XstsResponse?  {
        val xstsPayload = XstsPayload(
            properties = XstsProperties(
                sandboxId = sandboxId,
                userTokens = listOf(xBoxResponse.token)
            ),
            relyingParty = relyingParty,
            tokenType = TokenType.JWT
        )

        val response = httpClient.post {
            url(authUrl)
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(XstsPayload.serializer(), xstsPayload))
        }

        if (!response.status.isSuccess()) {
            onRequestError()
            return null
        }

        return json.decodeFromString(XstsResponse.serializer(), response.bodyAsText())
    }
}