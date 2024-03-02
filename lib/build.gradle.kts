/*
 * MIT License
 * Copyright 2024 Redtronics
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.serialization.json)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotest.multiplatform)
    `maven-publish`
}

group = "dev.redtronics.mokt"
version = System.getenv(/* name = */ "CI_COMMIT_TAG") ?: System.getenv(/* name = */ "CI_COMMIT_SHORT_SHA")?.let { "$it-dev" } ?: "0.0.0-DEV"

repositories {
    mavenCentral()
}

val minecraftApiUrl: String by project
val minecraftServiceUrl: String by project
val minecraftSessionUrl: String by project

val templateSrc = "src/commonMain/templates"
val templateDest: File = project.layout.buildDirectory.file("generated/templates").get().asFile
val templateProps: Map<String, Any> = mapOf(
    "minecraftApiUrl" to minecraftApiUrl,
    "minecraftServiceUrl" to minecraftServiceUrl,
    "minecraftSessionUrl" to minecraftSessionUrl
)


kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain {
            kotlin.srcDir(templateDest)
            dependencies {
                // Serialization
                api(libs.kotlinx.serialization.json)

                // Ktor Client
                api(libs.ktor.client.core)

                // Ktor Server
                api(libs.ktor.server.core)
                api(libs.ktor.server.cio)
                api(libs.ktor.server.content.negotiation)

                // Ktor Common
                api(libs.ktor.seralization.json)

                // UUID
                api(libs.uuid)
            }
        }
        commonTest {
            dependencies {
                // Kotest
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.property)

                // Ktor Mock
                implementation(libs.ktor.client.mock)
                implementation(libs.ktor.server.test.host)
            }
        }

        jvmTest {
            dependencies {
                // Kotest
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}

tasks {
    create<Copy>(name = "generateTemplates") {
        filteringCharset = "UTF-8"
        inputs.properties(templateProps)

        from(templateSrc)
        expand(templateProps)
        into(templateDest)
    }

    withType<KotlinCompile<*>> {
        dependsOn("generateTemplates")
    }

    named<Test>("jvmTest") {
        useJUnitPlatform()
        reports.junitXml.required = true
        systemProperty("gradle.build.dir", layout.buildDirectory.get().asFile.absolutePath)
        filter {
            isFailOnNoMatchingTests = false
        }
        testLogging {
            showExceptions = true
            showStandardStreams = true
            events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}

//
//koverReport {
//    filters {
//        includes {
//            packages("dev.redtronics.mokt")
//        }
//    }
//    defaults {
//        html {
//            setReportDir(layout.buildDirectory.dir("kover-reports/html-result"))
//            onCheck = true
//        }
//        xml {
//            setReportFile(layout.buildDirectory.file("kover-reports/result.xml"))
//            onCheck = true
//        }
//    }
//}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name = "Mokt"
                description = "A Kotlin SDK for the Mojang API."
                url = "https://redtronics.dev"

                developers {
                    developer {
                        name = "Redtronics"
                        id = "redtronics"
                        organization = "HuebCraft, Nyria"
                        timezone = "Europe/Berlin"
                    }
                }

                licenses {
                    license {
                        name = "MIT"
                        url = "https://gitlab.redtronics.dev/minecraft/mokt/-/blob/master/LICENSE?ref_type=heads"
                    }
                }

                issueManagement {
                    system = "GitLab"
                    url = "https://gitlab.redtronics.dev/minecraft/mokt/-/issues"
                }
            }
        }

        repositories {
            if (System.getenv(/* name = */ "CI_JOB_TOKEN") != null) {
                maven {
                    name = "GitLab"

                    val projectId = System.getenv("CI_PROJECT_ID")
                    val apiV4 = System.getenv("CI_API_V4_URL")
                    url = uri("$apiV4/projects/$projectId/packages/maven")

                    authentication {
                        create(/* name = */ "token", /* type = */ HttpHeaderAuthentication::class.java) {
                            credentials(HttpHeaderCredentials::class.java) {
                                name = "Job-Token"
                                value = System.getenv(/* name = */ "CI_JOB_TOKEN")
                            }
                        }
                    }
                }
            }
        }
    }
}
