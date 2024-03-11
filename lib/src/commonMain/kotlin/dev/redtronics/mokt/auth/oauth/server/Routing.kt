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

package dev.redtronics.mokt.auth.oauth.server

import dev.redtronics.mokt.auth.oauth.OAuthCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.channels.Channel
import kotlinx.html.HTML

fun Application.routing(
    channel: Channel<OAuthCode>,
    routePath: String,
    responsePage: HTML.() -> Unit = {}
) {
    routing {
        get(routePath) {
            val code = call.request.queryParameters["code"]
            val state = call.request.queryParameters["state"]

            if (code == null || state == null) {
                call.respondText("Invalid request")
                return@get
            }

            val authCode = OAuthCode(code)
            call.respondHtml(HttpStatusCode.OK, responsePage)
            channel.send(authCode)
        }
    }
}