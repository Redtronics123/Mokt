import kotlin.io.path.Path

plugins {
    `mokt-publishing`
    `mokt-multiplatform`
    `mokt-cinterop-generation`
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
