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

package dev.redtronics.mokt.provider

import io.ktor.http.*

/**
 * Central adapter for the authentication providers.
 *
 * @property name The name of the provider.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public interface Provider {
    public val name: String
    public val url: Url
    public val host: String
    public val port: Int
}
