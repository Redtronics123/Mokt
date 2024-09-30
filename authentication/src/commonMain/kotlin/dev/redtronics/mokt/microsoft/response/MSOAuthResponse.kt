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

package dev.redtronics.mokt.microsoft.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class OAuthCode(
    public val code: String,
    public val state: Int
)

@Serializable
public data class OAuthError(
    public val error: OAuthErrorItem,
    @SerialName("error_description")
    public val errorDescription: String
)

@Serializable
public enum class OAuthErrorItem(public val value: String) {
    INVALID_REQUEST("invalid_request"),
    UNAUTHORIZED_CLIENT("unauthorized_client"),
    ACCESS_DENIED("access_denied"),
    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type"),
    SERVER_ERROR("server_error"),
    TEMPORARY_UNAVAILABLE("temporarily_unavailable"),
    INVALID_RESOURCE("invalid_resource"),
    LOGIN_REQUIRED("login_required"),
    INTERACTION_REQUIRED("interaction_required");

    public companion object {
        public fun byName(name: String): OAuthErrorItem = entries.first { it.value == name }
    }
}