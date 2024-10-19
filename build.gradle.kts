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

plugins {
    org.jetbrains.dokka
}

version = System.getenv(/* name = */ "CI_COMMIT_TAG") ?: System.getenv(/* name = */ "CI_COMMIT_SHORT_SHA")?.let { "$it-dev" } ?: "0.0.0-DEV"

repositories {
    mavenCentral()
}
