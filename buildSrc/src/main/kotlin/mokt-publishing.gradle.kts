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

plugins {
    `maven-publish`
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name = Project.NAME
                description = Project.DESCRIPTION
                url = Project.URL
                inceptionYear = "2024"

                licenses {
                    license {
                        name = "MIT"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }

                developers {
                    developer {
                        id = "redtronics"
                        name = "Nils Jäkel"
                        timezone = "Europe/Berlin"
                        email = "nils.jaekel@proton.me"
                    }
                }

                ciManagement {
                    system = "Gitlab"
                }

                issueManagement {
                    system = "Gitlab"
                }

                scm {
                    connection = "${Project.GITLAB_URL}/nils.jaekel/mokt.git"
                    developerConnection = "${Project.GITLAB_URL}/nils.jaekel/mokt.git"
                    url = "${Project.GITLAB_URL}/nils.jaekel/mokt"
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
