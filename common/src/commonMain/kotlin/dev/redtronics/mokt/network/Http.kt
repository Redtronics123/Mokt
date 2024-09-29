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

package dev.redtronics.mokt.network

import io.ktor.client.*
import kotlinx.serialization.json.Json

/**
 * Mokt's HTTP client to interact with the Minecraft World (API).
 *
 * @since 0.0.1
 * @author Nils Jäkel
 * */
public expect val httpClient: HttpClient

public val defaultJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
    prettyPrint = true
}