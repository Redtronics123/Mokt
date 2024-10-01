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

public class MSDeviceAuthBuilder internal constructor(override val ms: Microsoft) : MSAuth {

    public suspend fun getAuthorizationCode(onRequestError: suspend (err: CodeErrorResponse) -> Unit = {}): MSDeviceCodeResponse {
        val response = ms.httpClient.submitForm(
            url = "https://login.microsoftonline.com/common/oauth2/v2.0/devicecode",
            formParameters = parameters {
                append("client_id", ms.clientId!!)
                append("scope", ms.scopes.joinToString(" ") { it.value })
            }
        )

        if (!response.status.isSuccess()) {
            onRequestError(ms.json.decodeFromString(CodeErrorResponse.serializer(), response.bodyAsText()))
        }
        return ms.json.decodeFromString(MSDeviceCodeResponse.serializer(), response.bodyAsText())
    }

    public fun build() {

    }
}