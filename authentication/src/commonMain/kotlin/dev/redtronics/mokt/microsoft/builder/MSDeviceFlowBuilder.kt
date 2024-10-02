/*
 * MIT License
 * Copyright 2024 Nils Jäkel
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software.
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package dev.redtronics.mokt.microsoft.builder

import dev.redtronics.mokt.microsoft.MSAuth
import dev.redtronics.mokt.microsoft.Microsoft
import dev.redtronics.mokt.microsoft.response.CodeErrorResponse
import dev.redtronics.mokt.microsoft.response.MSDeviceCodeResponse
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Builder to configure the Microsoft device authentication flow.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 * */
public class MSDeviceFlowBuilder internal constructor(override val ms: Microsoft) : MSAuth() {
    /**
     * The URL to the Microsoft Device Code endpoint.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     */
    public val deviceCodeEndpointUrl: Url
        get() = Url("https://login.microsoftonline.com/common/oauth2/v2.0/devicecode")

    /**
     * Requests an authorization code from the Microsoft Device Code endpoint.
     *
     * @param onRequestError The function to be called if an error occurs during the authorization code request.
     * @return The [MSDeviceCodeResponse] of the authorization code request or null if an error occurs.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public suspend fun requestAuthorizationCode(onRequestError: suspend (err: CodeErrorResponse) -> Unit = {}): MSDeviceCodeResponse? {
        val response = ms.httpClient.submitForm(
            url = deviceCodeEndpointUrl.toString(),
            formParameters = parameters {
                append("client_id", ms.clientId!!)
                append("scope", ms.scopes.joinToString(" ") { it.value })
            }
        )

        if (!response.status.isSuccess()) {
            onRequestError(ms.json.decodeFromString(CodeErrorResponse.serializer(), response.bodyAsText()))
            return null
        }
        return ms.json.decodeFromString(MSDeviceCodeResponse.serializer(), response.bodyAsText())
    }

    override fun build() {

    }
}
