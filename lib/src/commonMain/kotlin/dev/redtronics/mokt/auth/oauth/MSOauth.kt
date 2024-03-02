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
import io.ktor.http.cio.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.util.*
import kotlinx.coroutines.channels.Channel
import kotlinx.html.HTML
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

expect suspend fun MSOauth.openInBrowser(url: String)

class MSOauth(
    private val clientId: String,
    private val httpClient: HttpClient,
    private val port: Int = 59001,
    private val redirectPath: String = "/callback"
) {
    init {
        require(redirectPath.startsWith("/"))
    }

    private val redirectUrl = "http://localhost:$port$redirectPath"

    suspend fun getMSToken(openInBrowser: Boolean = false, responsePage: HTML.() -> Unit): MSTokenOauthResponse {
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
                append("client_id", clientId)
                append("response_type", "code")
                append("redirect_uri", redirectUrl)
                append("response_mode", "query")
                append("scope", "user.read")
                append("state", generateUniqueIdentifier())
            }
        }

        if (openInBrowser) openInBrowser(url)

        val authCode = channel.receive()
        val tokenResponse = httpClient.submitForm(encodeInQuery = false) {
            url {
                protocol = URLProtocol.HTTPS
                host = "login.microsoftonline.com"
                path("consumers", "oauth2", "v2.0", "token")
            }
            formData {
                append("client_id", clientId)
                append(key = "scope", value = "XBoxLive.signin offline_access email openid")
                append("code", authCode.code)
                append("redirect_uri", redirectUrl)
                append("grant_type", "authorization_code")
            }
        }

        if (!tokenResponse.status.isSuccess()) {
            val errorResponse: MSTokenOauthErrorResponse = tokenResponse.body()
            error("Failed to get token: ${tokenResponse.status}, ${errorResponse.error}: ${errorResponse.errorDescription}")
        }
        return tokenResponse.body<MSTokenOauthResponse>()
    }

    companion object {
        private fun generateUniqueIdentifier() = UUID.generateUUID().toString()
    }
}