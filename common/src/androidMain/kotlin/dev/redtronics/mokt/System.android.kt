package dev.redtronics.mokt

public actual fun getEnv(key: String): String? = System.getenv(key)

public actual val os: OsType = OsType.ANDROID
