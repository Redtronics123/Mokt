package dev.redtronics.mokt

public actual fun getEnv(key: String): String? = System.getenv(key)
