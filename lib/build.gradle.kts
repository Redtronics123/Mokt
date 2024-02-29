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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization.json)
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
}

val targetJavaVersion = 21
java {
    JavaVersion.toVersion(targetJavaVersion).let { javaVersion ->
        if (JavaVersion.current() < javaVersion) toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
}

val minecraftApiUrl: String by project

val templateSrc = "src/main/templates"
val templateDest: File = project.layout.buildDirectory.file("generated/templates").get().asFile
val templateProps: Map<String, Any> = mapOf(
    "minecraftApiUrl" to minecraftApiUrl
)

tasks {
    create<Copy>(name = "generateTemplates") {
        filteringCharset = "UTF-8"
        inputs.properties(templateProps)

        from(templateSrc)
        expand(templateProps)
        into(templateDest)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        dependsOn("generateTemplates")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = targetJavaVersion.toString()
        }
        dependsOn("generateTemplates")
    }
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir(templateDest)
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
