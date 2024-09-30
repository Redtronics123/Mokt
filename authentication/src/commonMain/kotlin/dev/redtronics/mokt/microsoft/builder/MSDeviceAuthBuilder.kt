/*
 * MIT License
 * Copyright 2024 Nils Jäkel
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software.
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package dev.redtronics.mokt.microsoft.builder

import dev.redtronics.mokt.microsoft.MSScopes
import dev.redtronics.mokt.microsoft.MSTenant
import dev.redtronics.mokt.microsoft.MsAuth
import io.ktor.client.*
import kotlinx.serialization.json.Json

public class MSDeviceAuthBuilder(
    override val tenant: MSTenant,
    override val scopes: List<MSScopes>,
    override val httpClient: HttpClient,
    override val json: Json
) : MsAuth() {
    public suspend fun accessToken() {

    }

    override fun build() {

    }
}