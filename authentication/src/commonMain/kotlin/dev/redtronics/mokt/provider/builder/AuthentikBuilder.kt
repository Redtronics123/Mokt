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

package dev.redtronics.mokt.provider.builder

import dev.redtronics.mokt.MojangGameAuth
import dev.redtronics.mokt.provider.Authentik
import dev.redtronics.mokt.provider.response.AccessResponse

public class AuthentikBuilder internal constructor(
    override val provider: Authentik
) : MojangGameAuth<Authentik>() {
    override fun accessToken(): AccessResponse? {
        TODO("Not yet implemented")
    }

    override fun build() {
        TODO("Not yet implemented")
    }
}