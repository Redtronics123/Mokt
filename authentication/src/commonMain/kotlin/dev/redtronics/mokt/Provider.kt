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

/**
 * Represents an authentication provider which is used for authentication flow.
 *
 * @property providerName The name of the provider.
 *
 * @author Nils Jäkel
 * @since 0.0.1
 */
public enum class Provider(public val providerName: String) {
    AUTHENTIK("authentik"),
    KEYCLOAK("keycloak"),
    STANDARD("standard");

    public companion object {
        /**
         * Finds a [Provider] by its [providerName].
         *
         * @param providerName The provider name to search for.
         * @return The [Provider] with the specified [providerName].
         * @throws NoSuchElementException If no [Provider] with the specified [providerName] is found.
         *
         * @since 0.0.1
         * @author Nils Jäkel
         */
        public fun byName(providerName: String): Provider = entries.first { it.providerName == providerName }
    }
}