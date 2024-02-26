package dev.redtronics

import kotlinx.coroutines.supervisorScope

suspend fun main() = supervisorScope {
    println("Hello World!")
}
