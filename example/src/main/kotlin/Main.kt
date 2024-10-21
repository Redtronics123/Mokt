package dev.redtronics

import dev.redtronics.mokt.Launcher
import kotlinx.coroutines.supervisorScope

suspend fun main(): Unit = supervisorScope {
    Launcher().test("Hello")
}
