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

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class XstsResponse(
    @SerialName("IssueInstant")
    public val issueInstant: Instant,
    @SerialName("NotAfter")
    public val notAfter: Instant,
    @SerialName("Token")
    public val token: String,
    @SerialName("DisplayClaims")
    public val displayClaims: DisplayClaims
)