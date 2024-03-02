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

package dev.redtronics.mokt.auth.oauth

import dev.redtronics.mokt.auth.oauth.response.OAuthCode
import dev.redtronics.mokt.auth.oauth.server.routing
import dev.redtronics.mokt.auth.oauth.server.setup
import io.ktor.client.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.channels.Channel
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

expect suspend fun MSOauth.openInBrowser(url: String)

@Serializable
data class AuthCode(val code: String)
