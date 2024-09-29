package dev.redtronics.mokt

import dev.redtronics.mokt.cinterop.get_env
import kotlinx.cinterop.toKString

public actual fun getEnv(key: String): String? {
    val value = get_env(key)?.toKString()
    if (value.isNullOrEmpty()) {
        return null
    }
    return value
}

public actual val os: OsType = OsType.WINDOWS
