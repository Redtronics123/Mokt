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

package dev.redtronics.mokt.payload

import dev.redtronics.mokt.builder.TokenType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class XBoxPayload(
    @SerialName("Properties")
    public val properties: XBoxProperties,
    public val relyingParty: String,
    public val tokenType: TokenType
)

@Serializable
public data class XBoxProperties(
    @SerialName("AuthMethod")
    public val xAuthMethod: String,
    @SerialName("SiteName")
    public val siteName: String,
    @SerialName("RpsTicket")
    public val rpsTicket: String
)