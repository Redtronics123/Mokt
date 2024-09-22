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

import dev.redtronics.mokt.Provider
import dev.redtronics.mokt.provider.builder.MSDeviceAuthBuilder
import dev.redtronics.mokt.provider.builder.MSOAuthBuilder

public class Microsoft : Provider {
    override val name: String
        get() = "Microsoft"

    public var clientId: String? = null

    public var authMethod: MSAuthMethod = MSAuthMethod.OAUTH2

    public suspend fun oauth2(builder: suspend MSOAuthBuilder.() -> Unit) {

    }

    public suspend fun device(builder: suspend MSDeviceAuthBuilder.() -> Unit) {

    }
}

public enum class MSAuthMethod(public val authMethodName: String) {
    OAUTH2("oauth2"),
    DEVICE_AUTH("device_auth");

    public companion object {
        public fun byName(name: String): MSAuthMethod = entries.first { it.authMethodName == name }
    }
}