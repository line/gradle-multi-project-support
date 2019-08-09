/*
 * Copyright 2019 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
}

gradlePlugin {
    plugins {
        register("recursiveGitLogPlugin") {
            id = "com.linecorp.recursive-git-log-plugin"
            group = "com.linecorp.support.project.multi"
            implementationClass = "com.linecorp.support.project.multi.log.git.recursive.RecursiveGitLogPlugin"
        }
    }

    dependencies {
        testImplementation(kotlin("test-junit5"))

        testImplementation(gradleTestKit())
        testImplementation(gradleApi())
        testImplementation(gradleKotlinDsl())
        testImplementation(kotlin("stdlib"))

        testImplementation("io.mockk:mockk:1.8.13")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.1")
        testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.19")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}

pluginBundle {
    website = "https://github.com/line/multi-project-support"
    vcsUrl = "https://github.com/line/multi-project-support"
    description = "A plugin for extract affected change log via git history and Gradle project dependency"

    (plugins) {
        "recursiveGitLogPlugin" {
            displayName = "Recursive Git Log Plugin"
            tags = listOf("multi-project", "multi-application", "history", "git")
        }
    }
}
