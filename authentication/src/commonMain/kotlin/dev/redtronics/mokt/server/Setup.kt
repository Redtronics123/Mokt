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

package dev.redtronics.mokt.server

import dev.redtronics.mokt.network.defaultJson
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

/**
 * Sets up the server with their default settings.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 * */
internal fun Application.setup() {
    install(ContentNegotiation) {
        json(json = defaultJson)
    }
}