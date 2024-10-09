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

package dev.redtronics.mokt.response

import kotlinx.serialization.SerialName

public data class MojangResponse(
    public val username: String,
    public val roles: List<String>,
    @SerialName("access_token")
    public val accessToken: String,
    @SerialName("token_type")
    public val tokenType: String,
    @SerialName("expires_in")
    public val expiresIn: Int
)
