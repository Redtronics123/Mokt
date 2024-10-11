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

package dev.redtronics.mokt.provider.builder

import dev.redtronics.mokt.MojangGameAuth
import dev.redtronics.mokt.provider.Microsoft
import dev.redtronics.mokt.network.interval
import dev.redtronics.mokt.openInBrowser
import dev.redtronics.mokt.provider.html.userCodePage
import dev.redtronics.mokt.provider.response.*
import dev.redtronics.mokt.provider.server.displayCodeRouting
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.date.*
import kotlinx.html.HTML
import kotlin.time.Duration.Companion.seconds

/**
 * Builder to configure the Microsoft device authentication flow.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 * */
public class DeviceBuilder internal constructor(override val provider: Microsoft) : MojangGameAuth<Microsoft>() {
    private var codeServer: CIOApplicationEngine? = null

    /**
     * The URL to the Microsoft Device Code endpoint.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     */
    public val deviceCodeEndpointUrl: Url
        get() = Url("https://login.microsoftonline.com/${provider.tenant.value}/oauth2/v2.0/devicecode")

    /**
     * The URL to the Microsoft Device Login endpoint.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     */
    public val deviceLoginEndpointUrl: Url
        get() = Url("https://www.microsoft.com/link")

    /**
     * The grant type of the Microsoft Device Code endpoint.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public val grantType: String
        get() = "urn:ietf:params:oauth:grant-type:device_code"


    public var displayServerForceHttps: Boolean = false

    public var displayServerUrl: Url = Url("http://localhost:18769/usercode")

    public var displayServerCodePage: HTML.(userCode: String) -> Unit = { userCode -> userCodePage(userCode) }

    public var display: suspend (userCode: String, url: Url) -> Unit = { userCode, url ->
        displayUserCodeInBrowser(userCode)
        openInBrowser(url)
    }

    public suspend fun displayUserCodeInBrowser(userCode: String) {
        codeServer = embeddedServer(CIO, displayServerUrl.port, displayServerUrl.host) {
            val path = displayServerUrl.fullPath.ifBlank { "/" }
            displayCodeRouting(userCode, path, displayServerCodePage)
        }

        codeServer!!.start()
        openInBrowser(displayServerUrl)
    }

    /**
     * Requests an authorization code from the Microsoft Device Code endpoint.
     *
     * @param onRequestError The function to be called if an error occurs during the authorization code request.
     * @return The [DeviceCodeResponse] of the authorization code request or null if an error occurs.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public suspend fun requestAuthorizationCode(
        onRequestError: suspend (err: CodeErrorResponse) -> Unit = {},
    ): DeviceCodeResponse? {
        val response = provider.httpClient.submitForm(
            url = deviceCodeEndpointUrl.toString(),
            formParameters = parameters {
                append("client_id", provider.clientId!!)
                append("scope", provider.scopes.joinToString(" ") { it.value })
            }
        )
        if (!response.status.isSuccess()) {
            onRequestError(provider.json.decodeFromString(CodeErrorResponse.serializer(), response.bodyAsText()))
            return null
        }
        return provider.json.decodeFromString(DeviceCodeResponse.serializer(), response.bodyAsText())
    }

    /**
     * Requests an access token from the Microsoft Device Login endpoint.
     *
     * @param deviceCodeResponse The [DeviceCodeResponse] of the authorization code request.
     * @param onRequestError The function to be called if an error occurs during the access token request.
     * @return The [AccessResponse] of the access token request or null if an error occurs.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public suspend fun requestAccessToken(
        deviceCodeResponse: DeviceCodeResponse,
        onRequestError: suspend (err: DeviceAuthStateError) -> Unit = {},
    ): AccessResponse? {
        display(deviceCodeResponse.userCode, deviceLoginEndpointUrl)

        val startTime = getTimeMillis()
        return authLoop(startTime, deviceCodeResponse, onRequestError)
    }

    /**
     * The loop to request an access token from the Microsoft Device Login endpoint.
     *
     * @param startTime The start time of the loop.
     * @param deviceCodeResponse The [DeviceCodeResponse] of the authorization code request.
     * @param onRequestError The function to be called if an error occurs during the access token request.
     * @return The [AccessResponse] of the access token request or null if an error occurs.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    private suspend fun authLoop(
        startTime: Long,
        deviceCodeResponse: DeviceCodeResponse,
        onRequestError: suspend (err: DeviceAuthStateError) -> Unit,
    ) = interval(
        interval = deviceCodeResponse.interval.seconds,
        cond = { getTimeMillis() - startTime < deviceCodeResponse.expiresIn * 1000 }
    ) {
        val response = provider.httpClient.submitForm(
            url = provider.tokenEndpointUrl.toString(),
            formParameters = parameters {
                append("client_id", provider.clientId!!)
                append("device_code", deviceCodeResponse.deviceCode)
                append("grant_type", grantType)
            }
        )

        val responseBody = response.bodyAsText()
        if (responseBody.contains("error")) {
            val errorResponse = provider.json.decodeFromString(DeviceAuthStateError.serializer(), responseBody)
            if (errorResponse.error != DeviceAuthStateErrorItem.AUTHORIZATION_PENDING) {
                onRequestError(errorResponse)
                cancel()
            }
            return@interval null
        }

        codeServer!!.stop()
        return@interval provider.json.decodeFromString(AccessResponse.serializer(), responseBody)
    }

    override fun accessToken(): AccessResponse? {
        TODO("Not yet implemented")
    }

    override fun build() {
        if (displayServerForceHttps && !displayServerUrl.protocol.isSecure()) throw IllegalArgumentException("Display server URL is not using HTTPS")
    }
}