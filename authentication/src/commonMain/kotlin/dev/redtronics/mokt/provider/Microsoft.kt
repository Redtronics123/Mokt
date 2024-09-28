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

/**
 * Microsoft authentication provider.
 * Interacts with the Microsoft API via device authentication or oauth2.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 * */
public class Microsoft : Provider {
    override val name: String
        get() = "Microsoft"

    /**
     * The client id for the Microsoft provider.
     *
     * If the client id is not set, the provider will try to get the client id
     * from the environment MS_CLIENT_ID.
     *
     * @throws IllegalArgumentException If the client id is not valid or null.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var clientId: String? = getEnv("MS_CLIENT_ID")

    /**
     * Detects which authentication method is used.
     * If no auth flow is started, this will be null.
     *
     * @see MSAuthMethod
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var authMethod: MSAuthMethod? = null
        private set

    /**
     * Configuration for the OAuth 2.0 authentication flow.
     *
     * @param builder The builder to configure the OAuth 2.0 flow.
     * @return The result of the builder [T].
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public suspend fun <T> oauth2(builder: suspend MSOAuthBuilder.() -> T): T {
        authMethod = MSAuthMethod.OAUTH2
        MSOAuthBuilder().apply { return builder() }
    }

    /**
     * Configuration for the device authentication flow.
     *
     * @param builder The builder to configure the device flow.
     * @return The result of the builder [T].
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
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