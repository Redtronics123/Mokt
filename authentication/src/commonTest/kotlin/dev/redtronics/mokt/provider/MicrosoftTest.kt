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

package dev.redtronics.mokt.provider

import dev.redtronics.mokt.auth
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe

class MicrosoftTest : FunSpec({
    test("client id validation") {
        auth<Microsoft> {
            clientId = "aa54f7f4-45f6-4f78-b95f-3b6cc98e0b7f"
        }
    }

    test("tenant changed by user") {
        val provider by auth<Microsoft> {
            clientId = "aa54f7f4-45f6-4f78-b95f-3b6cc98e0b7f"
            tenant = MSTenant.CONSUMERS
        }
        provider.tenant shouldBe MSTenant.CONSUMERS
    }

    test("scopes changed by user") {
        val provider by auth<Microsoft> {
            clientId = "aa54f7f4-45f6-4f78-b95f-3b6cc98e0b7f"
            scopes = listOf(MSScopes.XBOX_LIVE_SIGNIN, MSScopes.OFFLINE_ACCESS)
        }
        provider.scopes.shouldContainInOrder(MSScopes.XBOX_LIVE_SIGNIN, MSScopes.OFFLINE_ACCESS)
    }

    test("change device endpoint url if the tenant is changed") {
        val provider by auth<Microsoft> {
            clientId = "aa54f7f4-45f6-4f78-b95f-3b6cc98e0b7f"
            tenant = MSTenant.CONSUMERS
        }
        provider.tokenEndpointUrl.toString() shouldBe "https://login.microsoftonline.com/consumers/oauth2/v2.0/token"
    }

    test("use right auth method") {
        val provider by auth<Microsoft> {
            clientId = "aa54f7f4-45f6-4f78-b95f-3b6cc98e0b7f"
            device {}
        }
        provider.authMethod shouldBe MSAuthMethod.DEVICE_AUTH
    }

    test("getting auth method by name") {
        val authMethod = MSAuthMethod.byName("oauth2")
        authMethod shouldBe MSAuthMethod.OAUTH2
    }

    test("getting tenant by name") {
        val tenant = MSTenant.byName("consumers")
        tenant shouldBe MSTenant.CONSUMERS
    }

    test("getting scope by name") {
        val scope = MSScopes.byName("XBoxLive.signin")
        scope shouldBe MSScopes.XBOX_LIVE_SIGNIN
    }

    test("getting all scopes") {
        val scopes = MSScopes.allScopes
        MSScopes.entries.size shouldBe scopes.size
    }
})