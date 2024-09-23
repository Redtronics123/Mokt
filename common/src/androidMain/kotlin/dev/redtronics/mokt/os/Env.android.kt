package dev.redtronics.mokt.os

public actual fun getEnv(key: String): String? = System.getenv(key)