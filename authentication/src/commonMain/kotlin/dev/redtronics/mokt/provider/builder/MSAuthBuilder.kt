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

package dev.redtronics.mokt.provider.builder

import dev.redtronics.mokt.getEnv
import dev.redtronics.mokt.openInBrowser
import io.ktor.http.*

public sealed class MsAuth {
    /**
     * Builds the authentication configuration and validates it.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    internal abstract fun build()
}

public class MSDeviceAuthBuilder : MsAuth() {
    override fun build() {

    }
}

public class MSOAuthBuilder internal constructor() : MsAuth() {
    /**
     * The local redirect URL.
     *
     * On default, it will try to get the url from the environment variable `LOCAL_REDIRECT_URL`.
     * Otherwise, the url `http://localhost:8080` will be used.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var localRedirectUrl: Url = Url(getEnv("LOCAL_REDIRECT_URL") ?: "http://localhost:8080")

    /**
     * The port of the local redirect URL.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public val localRedirectPort: Int
        get() = localRedirectUrl.port

    /**
     * The given host of the local redirect URL.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public val localRedirectHost: String
        get() = localRedirectUrl.host

    /**
     * Checks if the local redirect URL is using HTTPS.
     * If this is not the case, the validation check will throw an exception.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var requireHttpsByRedirect: Boolean = false

    public suspend fun accessToken(
        browser: suspend (url: Url) -> Unit = {  url -> openInBrowser(url) }
    ) {

    }

    override fun build() {
        if (requireHttpsByRedirect && !localRedirectUrl.protocol.isSecure()) throw IllegalArgumentException("Local redirect URL is not using HTTPS")
    }
}