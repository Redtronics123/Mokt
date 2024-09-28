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
import io.ktor.http.*

public sealed class MsAuth {

}

public class MSDeviceAuthBuilder : MsAuth() {

}

public class MSOAuthBuilder internal constructor() : MsAuth() {
    public var localRedirectUrl: Url = Url(getEnv("LOCAL_REDIRECT_URL") ?: "http://localhost:8080")

    public val localRedirectPort: Int
        get() = localRedirectUrl.port

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

    public suspend fun msAccessToken() {

    }
}