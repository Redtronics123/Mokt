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

package dev.redtronics.mokt.provider

import dev.redtronics.mokt.getEnv
import dev.redtronics.mokt.provider.builder.MSDeviceAuthBuilder
import dev.redtronics.mokt.provider.builder.MSOAuthBuilder
import io.ktor.http.*

public class Microsoft(
    override var url: Url
) : Provider {
    override val name: String
        get() = "Microsoft"

    override val host: String
        get() = url.host

    override val port: Int
        get() = url.port

    public var useHttpsByRedirect: Boolean = false

    public var clientId: String? = getEnv("MS_CLIENT_ID")

    public var authMethod: MSAuthMethod? = null
        private set

    public suspend fun <T> oauth2(builder: suspend MSOAuthBuilder.() -> T): T {
        authMethod = MSAuthMethod.OAUTH2
        MSOAuthBuilder().apply { return builder() }
    }

    public suspend fun <T> device(builder: suspend MSDeviceAuthBuilder.() -> T): T {
        authMethod = MSAuthMethod.DEVICE_AUTH
        MSDeviceAuthBuilder().apply { return builder() }
    }
}


/**
 * Defines the different authentication methods used by the Microsoft provider.
 *
 * @property authMethodName The name of the authentication method.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public enum class MSAuthMethod(public val authMethodName: String) {
    /**
     * The OAuth 2.0 authentication method.
     * */
    OAUTH2("oauth2"),

    /**
     * The device authentication method.
     * */
    DEVICE_AUTH("device_auth");

    public companion object {
        /**
         * Finds an [MSAuthMethod] by its name.
         *
         * @param name The name of the authentication method.
         * @return The [MSAuthMethod] with the given name.
         *
         * @throws NoSuchElementException If no authentication method is found with the given name.
         *
         * @since 0.0.1
         * @author Nils Jäkel
         */
        public fun byName(name: String): MSAuthMethod = entries.first { it.authMethodName == name }
    }
}