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

private val architectures = listOf("x86_64-unknown-linux-gnu", "aarch64-unknown-linux-gnu", "x86_64-pc-windows-gnu")

internal fun Project.compileCppBindings() {
    exec {
        workingDir = file("../mokt-cpp-bindings")
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