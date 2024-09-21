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

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import java.nio.file.Path

private val architectures = listOf("aarch64-unknown-linux-gnu", "x86_64-pc-windows-gnu")

internal fun Project.compileCppBindings() {
    val workDir = file("../mokt-cpp-bindings")

    exec {
        workingDir = workDir
        commandLine = listOf("cmake", ".")
    }
    exec {
        workingDir = workDir
        commandLine = listOf("cmake", "--build", ".", "--config", "Release")
    }
}

internal fun Project.generateCInteropDefFiles() {
    val cppBindingsDir = file("../mokt-cpp-bindings")
    val includeDir = cppBindingsDir.resolve("include")
    val nativeCInterOpDir = file("../native-cinterop")

    val hFiles = includeDir.list()?.joinToString(" ") ?: error("Could not find any files in $includeDir")

    architectures.forEach { arch ->
        val defFile = nativeCInterOpDir.resolve("$arch.def")
        if (!defFile.exists()) {
            defFile.createNewFile()
        }

        defFile.writeText("""
            headers = $hFiles
            staticLibraries = libmokt_cpp_bindings.a
            compilerOpts = -I$includeDir
            libraryPaths = $cppBindingsDir
        """.trimIndent())
    }
}

fun KotlinNativeTargetWithHostTests.applyCInteropGeneration(path: Path) {
    compilations.getByName("main") {
        cinterops {
            create("moktCppBindings") {
                defFile(path)
                packageName("moktcpp")
            }
        }
    }
}
