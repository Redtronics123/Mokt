/*
 * MIT License
 * Copyright 2024 Nils Jäkel & David Ernst
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software.
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package dev.redtronics.mokt.provider.builder.device

import dev.redtronics.mokt.openInBrowser
import dev.redtronics.mokt.provider.html.WebTheme
import dev.redtronics.mokt.provider.html.userCodePage
import dev.redtronics.mokt.provider.server.displayCodeRouting
import io.ktor.http.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.html.HTML

public class UserCodeBuilder internal constructor(
    private val userCode: String
) {
    private var codeServer: CIOApplicationEngine? = null

    public var localServerUrl: Url = Url("http://localhost:18769/usercode")

    /**
     * The URL to the Microsoft Device Login endpoint.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     */
    public val deviceLoginEndpointUrl: Url
        get() = Url("https://www.microsoft.com/link")

    public var webPageTheme: WebTheme = WebTheme.DARK

    public var webPage: HTML.(userCode: String) -> Unit = { userCode -> userCodePage(userCode, webPageTheme) }

    public var forceHttps: Boolean = false

    public var shouldDisplayCode: Boolean = true

    public var setUserCodeAutomatically: Boolean = false

    public suspend fun displayUserCodeInBrowser() {
        codeServer = embeddedServer(CIO, localServerUrl.port, localServerUrl.host) {
            val path = localServerUrl.fullPath.ifBlank { "/" }
            displayCodeRouting(userCode, path, webPage)
        }

        codeServer!!.start()
        if (shouldDisplayCode) {
            openInBrowser(localServerUrl)
        }

        openInBrowser(deviceLoginEndpointUrl)
    }

    public fun displayUserCodeInTerminal() {

    }

    internal fun build(): CIOApplicationEngine? = codeServer
}

public enum class DisplayMode {
    BROWSER,
    TERMINAL
}