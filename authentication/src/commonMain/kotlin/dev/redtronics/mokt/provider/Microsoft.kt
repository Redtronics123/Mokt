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
import dev.redtronics.mokt.Provider
import dev.redtronics.mokt.network.client
import dev.redtronics.mokt.network.defaultJson
import dev.redtronics.mokt.provider.builder.DeviceBuilder
import dev.redtronics.mokt.provider.builder.GrantFlowBuilder
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

/**
 * Microsoft authentication provider.
 * Interacts with the Microsoft API via device authentication or oauth2.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 * */
public class Microsoft @PublishedApi internal constructor() : Provider {
    override val name: String
        get() = "Microsoft"

    /**
     * The http client used by the Microsoft provider.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    override var httpClient: HttpClient = client

    /**
     * The json used by the Microsoft provider.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    override var json: Json = defaultJson

    /**
     * The client id for the Microsoft provider.
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
     * The [MSTenant] value in the path of the request URL can be used to control
     * who can sign in to the application. For guest scenarios where you sign in a user from one tenant into another tenant,
     * you must provide the tenant identifier to sign them into the target tenant.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var tenant: MSTenant = MSTenant.CONSUMERS

    /**
     * The scopes of the Microsoft provider.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var scopes: List<MSScopes> = MSScopes.allScopes

    /**
     * The url of the token endpoint.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public val tokenEndpointUrl: Url
        get() = Url("https://login.microsoftonline.com/${tenant.value}/oauth2/v2.0/token")

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
    public suspend fun <T> defaultGrant(builder: suspend GrantFlowBuilder.() -> T): T {
        authMethod = MSAuthMethod.OAUTH2

        val oauthBuilder = GrantFlowBuilder(this)
        return builder(oauthBuilder).apply { oauthBuilder.build() }
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
    public suspend fun <T> device(builder: suspend DeviceBuilder.() -> T): T {
        authMethod = MSAuthMethod.DEVICE_AUTH

        val deviceAuthBuilder = DeviceBuilder(this)
        return builder(deviceAuthBuilder).apply { deviceAuthBuilder.build() }
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

/**
 * Defines the different tenants used by the Microsoft provider.
 *
 * @property value The name of the tenant.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public enum class MSTenant(public val value: String) {
    CONSUMERS("consumers"),
    ORGANIZATIONS("organizations"),
    COMMON("common");

    public companion object {
        /**
         * Finds an [MSTenant] by its name.
         *
         * @param name The name of the tenant.
         * @return The [MSTenant] with the given name.
         *
         * @throws NoSuchElementException If no tenant is found with the given name.
         *
         * @since 0.0.1
         * @author Nils Jäkel
         */
        public fun byName(name: String): MSTenant = entries.first { it.value == name }
    }
}

/**
 * Defines the different auth scopes used by the Microsoft provider.
 *
 * @property value The name of the scope.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public enum class MSScopes(public val value: String) {
    OPENID("openid"),
    PROFILE("profile"),
    EMAIL("email"),
    OFFLINE_ACCESS("offline_access"),
    XBOX_LIVE_SIGNIN("XBoxLive.signin");

    public companion object {
        /**
         * Finds an [MSTenant] by its name.
         *
         * @param name The name of the tenant.
         * @return The [MSTenant] with the given name.
         *
         * @throws NoSuchElementException If no tenant is found with the given name.
         *
         * @since 0.0.1
         * @author Nils Jäkel
         */
        public fun byName(name: String): MSScopes = entries.first { it.value == name }

        public val allScopes: List<MSScopes>
            get() = entries.toList()
    }
}