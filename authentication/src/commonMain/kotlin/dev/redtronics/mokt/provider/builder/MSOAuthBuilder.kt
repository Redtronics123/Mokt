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

import dev.redtronics.mokt.getEnv
import dev.redtronics.mokt.openInBrowser
import dev.redtronics.mokt.provider.MSScopes
import dev.redtronics.mokt.provider.MSTenant
import dev.redtronics.mokt.provider.MsAuth
import dev.redtronics.mokt.response.OAuthCode
import dev.redtronics.mokt.response.OAuthError
import dev.redtronics.mokt.server.oauthRouting
import dev.redtronics.mokt.server.setup
import io.ktor.http.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.util.*
import kotlinx.coroutines.channels.Channel
import kotlinx.html.HTML

public class MSOAuthBuilder internal constructor(
    override val tenant: MSTenant,
    override val scopes: List<MSScopes>
) : MsAuth() {
    /**
     * The local redirect URL. On default, it will try to get the url from the environment variable `LOCAL_REDIRECT_URL`.
     * Otherwise, the url `http://localhost:8080` will be used.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var localRedirectUrl: Url = Url(getEnv("LOCAL_REDIRECT_URL") ?: "http://localhost:8080")


    public val authorizeEndpointUrl: Url
        get() = Url("https://login.microsoftonline.com/${tenant.value}/oauth2/v2.0/authorize")


    public val tokenEndpointUrl: Url
        get() = Url("https://login.microsoftonline.com/${tenant.value}/oauth2/v2.0/token")

     /**
     * Checks if the local redirect URL is using HTTPS.
     * If this is not the case, the validation check will throw an exception.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var requireHttpsByRedirect: Boolean = false

    public var successRedirectPage: HTML.() -> Unit = {}

    public var failureRedirectPage: HTML.() -> Unit = {}

    public suspend fun accessToken(
        browser: suspend (url: Url) -> Unit = { url -> openInBrowser(url) },
        onRequestError: suspend (err: OAuthError) -> Unit = {}
    ) {
        val authCodeChannel: Channel<OAuthCode> = Channel()
        val path = localRedirectUrl.fullPath.ifBlank { "/" }

        val authServer = embeddedServer(CIO, localRedirectUrl.port, localRedirectUrl.toString()) {
            setup()
            oauthRouting(path, authCodeChannel, successRedirectPage, failureRedirectPage, onRequestError)
        }
        authServer.start()

        val msEndpointUrl = url {
            protocol = URLProtocol.HTTPS
            host = authorizeEndpointUrl.host
            parameters {

            }
        }

        browser(Url(msEndpointUrl))
    }

    override fun build() {
        if (requireHttpsByRedirect && !localRedirectUrl.protocol.isSecure()) throw IllegalArgumentException("Local redirect URL is not using HTTPS")
    }
}