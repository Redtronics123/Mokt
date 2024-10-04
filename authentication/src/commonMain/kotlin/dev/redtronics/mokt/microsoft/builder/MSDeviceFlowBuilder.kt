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
import dev.redtronics.mokt.microsoft.response.*
import dev.redtronics.mokt.network.interval
import dev.redtronics.mokt.openInBrowser
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.date.*
import kotlin.time.Duration.Companion.seconds

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
        get() = Url("https://login.microsoftonline.com/${ms.tenant.value}/oauth2/v2.0/devicecode")

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
    public var grantType: String = "urn:ietf:params:oauth:grant-type:device_code"

    /**
     * The way how to display the authorization code to the user.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var display: suspend (deviceCodeResponse: DeviceCodeResponse) -> Unit = {}

    /**
     * Opens the browser to display the Microsoft Device Code Page.
     * Only if the [DisplayMode.BROWSER] is set.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var browser: suspend (url: Url) -> Unit = { url -> openInBrowser(url) }

    /**
     * Used the terminal to display the auth information to the user.
     * Only if the [DisplayMode.TERMINAL] is set.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var terminal: suspend () -> Unit = {}

    /**
     * Requests an authorization code from the Microsoft Device Code endpoint.
     *
     * @param onRequestError The function to be called if an error occurs during the authorization code request.
     * @return The [DeviceCodeResponse] of the authorization code request or null if an error occurs.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public suspend fun requestAuthorizationCode(onRequestError: suspend (err: CodeErrorResponse) -> Unit = {}): DeviceCodeResponse? {
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
        return ms.json.decodeFromString(DeviceCodeResponse.serializer(), response.bodyAsText())
    }

    /**
     * Requests an access token from the Microsoft Device Login endpoint.
     *
     * @param displayMode The way how to display the authorization code to the user.
     * @param deviceCodeResponse The [DeviceCodeResponse] of the authorization code request.
     * @param onRequestError The function to be called if an error occurs during the access token request.
     * @return The [DeviceAccessResponse] of the access token request or null if an error occurs.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public suspend fun requestAccessToken(
        displayMode: DisplayMode,
        deviceCodeResponse: DeviceCodeResponse,
        onRequestError: suspend (err: DeviceAuthStateError) -> Unit = {},
    ): DeviceAccessResponse? {
        display(deviceCodeResponse)
        when (displayMode) {
            DisplayMode.BROWSER -> browser(deviceLoginEndpointUrl)
            DisplayMode.TERMINAL -> terminal()
        }

        val startTime = getTimeMillis()
        return authLoop(startTime, deviceCodeResponse, onRequestError)
    }

    /**
     * The loop to request an access token from the Microsoft Device Login endpoint.
     *
     * @param startTime The start time of the loop.
     * @param deviceCodeResponse The [DeviceCodeResponse] of the authorization code request.
     * @param onRequestError The function to be called if an error occurs during the access token request.
     * @return The [DeviceAccessResponse] of the access token request or null if an error occurs.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    private suspend fun authLoop(
        startTime: Long,
        deviceCodeResponse: DeviceCodeResponse,
        onRequestError: suspend (err: DeviceAuthStateError) -> Unit
    ) = interval(
        interval = deviceCodeResponse.interval.seconds,
        cond = { getTimeMillis() - startTime < deviceCodeResponse.expiresIn * 1000 },
    ) {
        val response = ms.httpClient.submitForm(
            url = ms.tokenEndpointUrl.toString(),
            formParameters = parameters {
                append("client_id", ms.clientId!!)
                append("device_code", deviceCodeResponse.deviceCode)
                append("grant_type", grantType)
            }
        )

        val responseBody = response.bodyAsText()
        if (responseBody.contains("error")) {
            val errorResponse = ms.json.decodeFromString(DeviceAuthStateError.serializer(), responseBody)
            if (errorResponse.error != DeviceAuthStateErrorItem.AUTHORIZATION_PENDING) {
                onRequestError(errorResponse)
                cancel()
            }
            return@interval null
        }
        return@interval ms.json.decodeFromString(DeviceAccessResponse.serializer(), responseBody)
    }

    override fun build() {

    }
}

/**
 * The way how to display the authorization code to the user.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 * */
public enum class DisplayMode {
    BROWSER,
    TERMINAL;
}
