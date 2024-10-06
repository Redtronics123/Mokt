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

package dev.redtronics.mokt.builder

import dev.redtronics.mokt.response.XBoxResponse
import dev.redtronics.mokt.response.XstsResponse
import io.ktor.client.*
import kotlinx.serialization.json.Json

public class XstsBuilder internal constructor(
    override val httpClient: HttpClient,
    override val json: Json,
    xBoxResponse: XBoxResponse
) : BaseBuilder() {
    internal suspend fun build(onRequestError: suspend () -> Unit): XstsResponse?  {
        return null
    }
}