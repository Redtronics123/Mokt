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

import org.gradle.api.Project as GradleProject
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import java.nio.file.Path

/**
 * The architectures for which cinterop def files should be generated.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
private val architectures = listOf("aarch64-unknown-linux-gnu", "x86_64-pc-windows-gnu")

/**
 * Compiles the C++ bindings for the cinterop.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
internal fun GradleProject.compileRustBindings() {
    val workDir = file("../mokt-rust-bindings")

    exec {
        workingDir = workDir
        commandLine = listOf("cargo", "build", "--release")
    }
}

/**
 * Generates cinterop def files for all supported operating system platforms.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
internal fun GradleProject.generateCInteropDefFiles() {
    val rustBindingsDir = file("../mokt-rust-bindings")
    val includeDir = rustBindingsDir.resolve("include")
    val nativeCInteropDir = file("../native-cinterop")

    val hFiles = includeDir.list()?.joinToString(" ") ?: throw Exception("No include files found")

    architectures.forEach { arch ->
        val defFile = nativeCInteropDir.resolve("$arch.def")
        if (!defFile.exists()) {
            defFile.createNewFile()
        }

        defFile.writeText("""
            headers = $hFiles
            staticLibraries = libmokt_rust_bindings.a
            compilerOpts = -I$includeDir
            libraryPaths = ${rustBindingsDir.resolve("target/release")}
        """.trimIndent())
    }
}

/**
 * Applies the cinterop generation for the given [architectures] to the given [KotlinNativeTargetWithHostTests].
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
fun KotlinNativeTargetWithHostTests.applyCInteropGeneration(path: Path) {
    compilations.getByName("main") {
        cinterops {
            create("moktRustBindings") {
                defFile(path)
                packageName("${Project.GROUP}.cinterop")
            }
        }
    }
}
