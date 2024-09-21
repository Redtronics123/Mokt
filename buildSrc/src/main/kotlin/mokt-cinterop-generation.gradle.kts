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

import org.jetbrains.gradle.ext.IdeaExtPlugin
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.TaskTriggersConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    idea
    eclipse
    org.jetbrains.gradle.plugin.`idea-ext`
}

if (!rootProject.pluginManager.hasPlugin("org.jetbrains.gradle.plugin.idea-ext")) {
    rootProject.pluginManager.apply(IdeaExtPlugin::class)
}

tasks {
    register("generateCInteropDefFiles") {
        group = Project.NAME.lowercase()
        description = "Generates cinterop def files for all supported operating system platforms."

        project.compileCppBindings()
        project.generateCInteropDefFiles()
    }

    withType<KotlinNativeCompile> {
        dependsOn("commonizeCInterop")
    }
}

rootProject.idea.project {
    this as ExtensionAware
    configure<ProjectSettings> {
        this as ExtensionAware
        configure<TaskTriggersConfig> {
            afterSync(tasks["generateCInteropDefFiles"])
        }
    }
}

eclipse.synchronizationTasks("generateCInteropDefFiles")
