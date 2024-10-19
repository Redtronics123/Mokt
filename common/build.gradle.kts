/*
 * MIT License
 * Copyright 2024 Nils Jäkel & David Ernst
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software.
 */

import kotlin.io.path.Path

plugins {
    `mokt-publishing`
    `mokt-multiplatform`
    `mokt-cinterop-generation`
    `mokt-android`
}

repositories {
    mavenCentral()
    google()
}

group = Project.GROUP

kotlin {
    js(IR) {
        generateTypeScriptDefinitions()
        nodejs()
        useEsModules()
        binaries.library()
    }

    linuxX64 {
        applyCInteropGeneration(Path("../native-cinterop/aarch64-unknown-linux-gnu.def"))
    }

    mingwX64 {
        applyCInteropGeneration(Path("../native-cinterop/x86_64-pc-windows-gnu.def"))
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.coroutines.debug)

                api(libs.ktor.serialization.json)
                api(libs.ktor.client.core)
                api(libs.ktor.client.logging)
                api(libs.ktor.client.content.negotiation)

                api(libs.kotlin.reflect)
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.property)
            }
        }

        jvmMain {
            dependencies {
                api(libs.kotlinx.coroutines.reactive)
                api(libs.ktor.client.cio)

                implementation(libs.slf4j.api)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotest.runner.junit5)
            }
        }

        jsMain {
            dependencies {
                api(libs.kotlinx.coroutines.core.js)
                api(libs.ktor.client.js)
                implementation(npm("open", "10.1.0"))
            }
        }

        linuxMain {
            dependencies {
                api(libs.ktor.client.cio)
            }
        }

        mingwMain {
            dependencies {
                api(libs.ktor.client.winhttp)
            }
        }

        androidMain {
            dependencies {
                api(libs.kotlinx.coroutines.android)
                api(libs.ktor.client.android)
                implementation(libs.androidx.browser)
            }
        }

        all {
            languageSettings {
               optIn("kotlinx.cinterop.UnsafeNumber")
               optIn("kotlinx.cinterop.ExperimentalForeignApi")
               optIn("kotlin.experimental.ExperimentalNativeApi")
               optIn("kotlin.native.runtime.NativeRuntimeApi")
               optIn("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}

android {
    namespace = group.toString()
}
