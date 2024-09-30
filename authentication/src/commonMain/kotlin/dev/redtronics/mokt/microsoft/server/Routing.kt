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

package dev.redtronics.mokt.microsoft.server

import dev.redtronics.mokt.microsoft.response.OAuthCode
import dev.redtronics.mokt.microsoft.response.OAuthError
import dev.redtronics.mokt.microsoft.response.OAuthErrorItem
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.coroutines.channels.Channel
import kotlinx.html.HTML

/**
 * Module for routing the oauth local redirect.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
internal fun Application.oauthRouting(
    redirectPath: String,
    channel: Channel<OAuthCode>,
    successPage: HTML.() -> Unit,
    failurePage: HTML.() -> Unit,
    onError: suspend (OAuthError) -> Unit
) {
    routing {
        get(redirectPath) {
            val queryParams = call.request.queryParameters
            if (queryParams["code"] == null || queryParams["state"] == null) {
                val oauthErrorCode = OAuthError(
                    error = OAuthErrorItem.byName(queryParams["error"]!!),
                    errorDescription = queryParams["error_description"]!!
                )

                call.respondHtml(HttpStatusCode.ExpectationFailed, failurePage)
                onError(oauthErrorCode)
                return@get
            }

            val oauthCode = OAuthCode(queryParams["code"]!!, queryParams["state"]!!.toInt())
            call.respondHtml(HttpStatusCode.OK, successPage)
            channel.send(oauthCode)
        }
    }
}