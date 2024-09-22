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

package dev.redtronics.mokt

import kotlin.reflect.KProperty

/**
 * Provides a way to access the selected authentication provider.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public class AuthProvider<in T : Provider> @PublishedApi internal constructor(private val provider: T) {
    public val value: @UnsafeVariance T
        get() = provider

    public operator fun getValue(t: T?, property: KProperty<*>): @UnsafeVariance T {
        return provider
    }
}

/**
 * Provides a [AuthProvider] which can be used to access the selected provider and authenticate with them.
 *
 * @param T The provider that should be used.
 * @param builder The builder to configure the provider.
 * @return [AuthProvider] with the selected provider.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public suspend inline fun <reified T : Provider> auth(noinline builder: suspend T.() -> Unit): AuthProvider<T> = when (T::class) {
    Microsoft::class -> AuthProvider(Microsoft().apply { builder(this as T) })
    Authentik::class -> AuthProvider(Authentik().apply { builder(this as T) })
    Keycloak::class -> AuthProvider(Keycloak().apply { builder(this as T) })
    else -> { throw IllegalArgumentException("Provider ${T::class} is not supported") }
}

