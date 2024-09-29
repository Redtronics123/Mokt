package dev.redtronics.mokt

public actual fun getEnv(key: String): String? = System.getenv(key)

public actual val os: OsType by lazy {
    val osName = System.getProperty("os.name").lowercase()
    when {
        osName.contains(other = "win") -> OsType.WINDOWS
        osName.contains(other = "nix") || osName.contains(other = "nux") || osName.contains(other = "aix") -> OsType.LINUX
        else -> { OsType.UNKNOWN }
    }
}
