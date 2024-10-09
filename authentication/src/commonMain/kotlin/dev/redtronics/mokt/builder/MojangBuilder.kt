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

import dev.redtronics.mokt.payload.MojangPayload
import dev.redtronics.mokt.response.MojangResponse
import dev.redtronics.mokt.response.XstsResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

public class MojangBuilder internal constructor(
    override val httpClient: HttpClient,
    override val json: Json,
    private val xstsResponse: XstsResponse
) : BaseBuilder() {
    public val minecraftLoginEndpoint: Url
        get() = Url("https://api.minecraftservices.com/authentication/login_with_xbox")

    internal suspend fun build(onRequestError: suspend (response: HttpResponse) -> Unit): MojangResponse? {
        val payload = MojangPayload(
            identityToken = "XBL3.0 x=${xstsResponse.displayClaims.xui[0].uhs};${xstsResponse.token}"
        )

        val response = httpClient.post {
            url(minecraftLoginEndpoint)
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(MojangPayload.serializer(), payload))
        }

        if (!response.status.isSuccess()) {
            onRequestError(response)
            return null
        }

        return json.decodeFromString(MojangResponse.serializer(), response.bodyAsText())
    }
}
