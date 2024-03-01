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

package dev.redtronics.mokt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.ktor.client.*
import io.ktor.client.engine.mock.*

class TestBlockedServer: FunSpec({
    test("test to get blocked server without limit") {
        val mockHttpClient = HttpClient(MockEngine {
            respond(List(25) {"test"}.joinToString(separator = "\n"))
        })
        val mokt = Mokt.Public(mockHttpClient)
        val blockedServer = mokt.getBlockedServer(limit = 0)
        blockedServer.hashedAddresses.shouldHaveSize(25)
    }

    test("test to get blocked server with limit") {
        val mockHttpClient = HttpClient(MockEngine {
            respond(List(25) {"test"}.joinToString(separator = "\n"))
        })
        val mokt = Mokt.Public(mockHttpClient)
        val blockedServer = mokt.getBlockedServer(limit = 20)
        blockedServer.hashedAddresses.shouldHaveSize(20)
    }
})
