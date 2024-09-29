package dev.redtronics.mokt

public expect fun getEnv(key: String): String?

public enum class OsType(public val value: String) {
    WINDOWS("windows"),
    LINUX("linux"),
    ANDROID("android"),
    UNKNOWN("unknown");

    public companion object {
        public fun byName(name: String): OsType = entries.firstOrNull { it.value == name } ?: UNKNOWN
    }
}

public expect val os: OsType
