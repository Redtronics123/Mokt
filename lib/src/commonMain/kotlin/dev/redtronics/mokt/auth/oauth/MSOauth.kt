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

package dev.redtronics.mokt.auth.oauth

import dev.redtronics.mokt.auth.oauth.response.MSTokenOauthErrorResponse
import dev.redtronics.mokt.auth.oauth.response.MSTokenOauthResponse
import dev.redtronics.mokt.auth.oauth.response.OAuthCode
import dev.redtronics.mokt.auth.oauth.server.routing
import dev.redtronics.mokt.auth.oauth.server.setup
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.util.*
import kotlinx.coroutines.channels.Channel
import kotlinx.html.HTML
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class MSOauth(
    private val clientId: String,
    private val httpClient: HttpClient,
    private val port: Int = 59001,
    private val redirectPath: String = "/callback"
) {
    init {
        require(redirectPath.startsWith("/")) { "redirectPath must start with /" }
    }

    private val redirectUrl = "http://localhost:$port$redirectPath"

    suspend fun getMSToken(openInBrowser: (String) -> Unit, responsePage: HTML.() -> Unit = {}): MSTokenOauthResponse {
        val channel: Channel<OAuthCode> = Channel()
        val server = embeddedServer(CIO, port = port, host = "127.0.0.1") {
            setup()
            routing(channel, redirectPath, responsePage)
        }.start()

        val url = url {
            protocol = URLProtocol.HTTPS
            host = "login.microsoftonline.com"
            path("consumers", "oauth2", "v2.0", "authorize")
            parameters.apply {
                append(name = "client_id", value = clientId)
                append(name = "response_type", value = "code")
                append(name = "redirect_uri", value = redirectUrl)
                append(name = "response_mode", value = "query")
                append(name = "scope", value = "XBoxLive.signin offline_access email openid")
                append(name = "state", value = generateUniqueIdentifier())
            }
        }

        openInBrowser(url)

        val authCode = channel.receive()
        val tokenResponse = httpClient.submitForm(
            url = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token",
            formParameters = parameters {
                append(name = "client_id", value = clientId)
                append(name = "scope", value = "XBoxLive.signin offline_access email openid")
                append(name = "code", value = authCode.code)
                append(name = "redirect_uri", value = redirectUrl)
                append(name = "grant_type", value = "authorization_code")
            },
            encodeInQuery = false
        )

        if (!tokenResponse.status.isSuccess()) {
            val errorResponse: MSTokenOauthErrorResponse = tokenResponse.body()
            error("Failed to get token: ${tokenResponse.status}, ${errorResponse.error}: ${errorResponse.errorDescription}")
        }

        val response: MSTokenOauthResponse = tokenResponse.body()
        server.stop()

        return response
    }

    companion object {
        private fun generateUniqueIdentifier() = UUID.generateUUID().toString()
    }
}
