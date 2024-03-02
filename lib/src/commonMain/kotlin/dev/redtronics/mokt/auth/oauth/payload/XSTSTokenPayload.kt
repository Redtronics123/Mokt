/*
 * MIT License
 * Copyright 2024 Redtronics
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package dev.redtronics.mokt.auth.oauth.payload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XSTSOAuthTokenPayload(
    @SerialName("Properties")
    val properties: XSTSOAuthTokenProperties,
    @SerialName("RelyingParty")
    val relyingParty: String,
    @SerialName("TokenType")
    val tokenType: String
)

@Serializable
data class XSTSOAuthTokenProperties(
    @SerialName("SandboxId")
    val sandboxId: String,
    @SerialName("UserTokens")
    val userTokens: List<String>
)
