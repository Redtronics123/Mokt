package dev.redtronics.mokt.os

import dev.redtronics.mokt.cpp.get_env
import kotlinx.cinterop.cstr
import kotlinx.cinterop.toKString

public actual fun getEnv(key: String): String? {
    val env = get_env(key.cstr)
    val value = env!!.toKString()

    if (value.isNotEmpty()) {
        return null
    }
    return value
}