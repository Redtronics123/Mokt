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

package dev.redtronics.mokt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProviderTest : FunSpec({
    test("select the provider by the given name") {
        val provider = Provider.byName("authentik")
        provider shouldBe Provider.AUTHENTIK
    }
})
