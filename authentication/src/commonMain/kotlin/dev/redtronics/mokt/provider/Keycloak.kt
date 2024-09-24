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

import io.ktor.http.*

public class Keycloak : Provider {
    override val name: String
        get() = "Keycloak"

    override val url: Url
        get() = TODO("Not yet implemented")

    override val host: String
        get() = TODO("Not yet implemented")

    override val port: Int
        get() = TODO("Not yet implemented")
}