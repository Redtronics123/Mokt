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

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization.json)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    `maven-publish`
}

group = "dev.redtronics.mokt"
version = System.getenv(/* name = */ "CI_COMMIT_TAG") ?: System.getenv(/* name = */ "CI_COMMIT_SHORT_SHA")?.let { "$it-dev" } ?: "0.0.0-DEV"

repositories {
    mavenCentral()
}

dependencies {
    // Serialization
    api(libs.kotlinx.serialization.json)

    /* Ktor */
    // Client
    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.client.logging)

    // Server
    api(libs.ktor.server.core)
    api(libs.ktor.server.cio)
    api(libs.ktor.server.content.negotiation)

    // Common
    api(libs.ktor.seralization.json)

    // Codec
    implementation(libs.codec)

    // Test
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.ktor)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.junitxml)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.ktor.server.test.host)
}

val minecraftApiUrl: String by project
val minecraftServiceUrl: String by project
val minecraftSessionUrl: String by project

val templateSrc = "src/main/templates"
val templateDest: File = project.layout.buildDirectory.file("generated/templates").get().asFile
val templateProps: Map<String, Any> = mapOf(
    "minecraftApiUrl" to minecraftApiUrl,
    "minecraftServiceUrl" to minecraftServiceUrl,
    "minecraftSessionUrl" to minecraftSessionUrl
)

tasks {
    create<Copy>(name = "generateTemplates") {
        filteringCharset = "UTF-8"
        inputs.properties(templateProps)

        from(templateSrc)
        expand(templateProps)
        into(templateDest)
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
        dependsOn("generateTemplates")
    }

    test {
        useJUnitPlatform()

        reports {
            junitXml.required = false
        }

        systemProperty("gradle.build.dir", project.layout.buildDirectory.asFile.get().absolutePath)
    }

    withType<DokkaTask> {
        outputDirectory = layout.buildDirectory.dir("dokka")
    }

    create<Jar>("javadocJar") {
        group = "build"
        archiveClassifier.set("javadoc")
        from(layout.buildDirectory.dir("dokka"))
        dependsOn("dokkaJavadoc")
    }

    create<Jar>("sourcesJar") {
        group = "build"
        archiveClassifier.set("sources")
        from(kotlin.sourceSets["main"].kotlin.srcDirs)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(8)
    sourceSets {
        main {
            kotlin.srcDir(templateDest)
        }
    }
}

koverReport {
    filters {
        includes {
            packages("dev.redtronics.mokt")
        }
    }
    defaults {
        html {
            setReportDir(layout.buildDirectory.dir("kover-reports/html-result"))
            onCheck = true
        }
        xml {
            setReportFile(layout.buildDirectory.file("kover-reports/result.xml"))
            onCheck = true
        }
    }
}

publishing {
    publications {
        create<MavenPublication>(name = "mokt") {
            from(components["java"])
            artifactId = "mokt"
        }

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
