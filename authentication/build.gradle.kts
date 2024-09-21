plugins {
    `mokt-publishing`
    `mokt-multiplatform`
}

group = Project.GROUP

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common"))
            }
        }
    }
}
