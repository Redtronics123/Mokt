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

package dev.redtronics.mokt.auth

import dev.redtronics.mokt.auth.common.payload.MojangAuthPayload
import dev.redtronics.mokt.auth.common.payload.XSTSOAuthTokenProperties
import dev.redtronics.mokt.auth.common.payload.XSTSTokenPayload
import dev.redtronics.mokt.auth.common.response.MojangAuthResponse
import dev.redtronics.mokt.auth.common.response.XBoxAuthResponse
import dev.redtronics.mokt.auth.common.response.XSTSAuthResponse
import dev.redtronics.mokt.auth.oauth.*
import dev.redtronics.mokt.auth.oauth.html.defaultResponsePage
import dev.redtronics.mokt.auth.oauth.server.routing
import dev.redtronics.mokt.auth.oauth.server.setup
import dev.redtronics.mokt.http.requireSuccessful
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.request.headers
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.util.*
import kotlinx.coroutines.channels.Channel
import kotlinx.html.HTML
import kotlinx.serialization.json.Json
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

sealed class MSAuth(
    private val httpClient: HttpClient,
    private val json: Json
) {
    suspend fun getXboxToken(msToken: String): XBoxAuthResponse {
        val xboxOAuthPayload = XBoxOAuthPayload(
            properties = XboxAuthProperties(
                authMethod = "RPS",
                siteName = "user.auth.xboxlive.com",
                rpsTicket = "d=$msToken"
            ),
            replyingParty = "http://auth.xboxlive.com",
            tokenType = "JWT"
        )

        val xboxAuthResponse = httpClient.post {
            headers {
                contentType(ContentType.Application.Json)
            }
            url(urlString = "https://user.auth.xboxlive.com/user/authenticate")
            setBody(xboxOAuthPayload)
        }

        xboxAuthResponse.requireSuccessful()
        return json.decodeFromString(xboxAuthResponse.bodyAsText())
    }

    suspend fun getXstsToken(xBoxAuthResponse: XBoxAuthResponse): XSTSAuthResponse {
        val xstsTokenPayload = XSTSTokenPayload(
            properties = XSTSOAuthTokenProperties(
                sandboxId = "RETAIL",
                userTokens = listOf(xBoxAuthResponse.token)
            ),
            relyingParty = "rp://api.minecraftservices.com/",
            tokenType = "JWT"
        )

        val xstsTokenResponse = httpClient.post {
            headers {
                contentType(ContentType.Application.Json)
            }
            url(urlString = "https://xsts.auth.xboxlive.com/xsts/authorize")
            setBody(xstsTokenPayload)
        }

        xstsTokenResponse.requireSuccessful()
        return json.decodeFromString(xstsTokenResponse.bodyAsText())
    }

    suspend fun getMojangAuthToken(xstsAuthResponse: XSTSAuthResponse): MojangAuthResponse {
        val mojangAuthPayload = MojangAuthPayload(
            identityToken = "XBL3.0 x=${xstsAuthResponse.displayClaim.xui[0].uhs};${xstsAuthResponse.token}",
            ensureLegacyEnabled = false
        )

        val minecraftResponse = httpClient.post {
            headers {
                contentType(ContentType.Application.Json)
            }
            url("https://api.minecraftservices.com/authentication/login_with_xbox")
            setBody(mojangAuthPayload)
        }

        minecraftResponse.requireSuccessful()
        return json.decodeFromString(minecraftResponse.bodyAsText())
    }

    class OAuth(
        private val clientId: String,
        private val httpClient: HttpClient,
        private val json: Json = Json {
            ignoreUnknownKeys = true
        },
        private val port: Int = 59001,
        private val redirectPath: String = "/callback",
    ) : MSAuth(httpClient, json) {
        init {
            require(redirectPath.startsWith("/")) { "redirectPath must start with /" }
        }

        private val redirectUrl = "http://localhost:$port$redirectPath"

        suspend fun getMSAccessToken(
            openInBrowser: suspend (String) -> Unit,
            responsePage: HTML.() -> Unit = { defaultResponsePage() },
        ): MSTokenOauthResponse {
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

            tokenResponse.requireSuccessful()
            val response: MSTokenOauthResponse = json.decodeFromString(tokenResponse.bodyAsText())

            server.stop()
            return response
        }

        suspend fun getMSAccessTokenFromRefreshToken(refreshToken: String): MSTokenOauthResponse {
            val accessTokenResponse = httpClient.submitForm(
                url = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token",
                formParameters = parameters {
                    append(name = "client_id", value = clientId)
                    append(name = "scope", value = "XBoxLive.signin offline_access email openid")
                    append(name = "refresh_token", value = refreshToken)
                    append(name = "grant_type", value = "refresh_token")
                },
                encodeInQuery = false
            )
            accessTokenResponse.requireSuccessful()

            return json.decodeFromString(accessTokenResponse.bodyAsText())
        }
    }

    companion object {
        private fun generateUniqueIdentifier() = UUID.generateUUID().toString()
    }
}