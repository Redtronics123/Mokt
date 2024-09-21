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
            }
        }

        jvmMain {
            dependencies {
                api(libs.kotlinx.coroutines.reactive)
            }
        }

        jsMain {
            dependencies {
                api(libs.kotlinx.coroutines.core.js)
            }
        }

        androidMain {
            dependencies {
                api(libs.kotlinx.coroutines.android)
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