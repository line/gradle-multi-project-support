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

gradlePlugin {
    plugins {
        register("recursiveGitLogPlugin") {
            id = "com.linecorp.recursive-git-log-plugin"
            group = "com.linecorp.support.project.multi"
            implementationClass = "com.linecorp.support.project.multi.log.git.recursive.RecursiveGitLogPlugin"
        }
    }
}

pluginBundle {
    description = "A plugin for extract affected change log via git history and Gradle project dependency"

    (plugins) {
        "recursiveGitLogPlugin" {
            displayName = "Recursive Git Log Plugin"
            tags = listOf("multi-project", "multi-application", "history", "git")
        }
    }
}

tasks {
    val prepareSubmodule by registering(Exec::class) {
        commandLine("git", "submodule", "update", "--init")
    }

    withType<Test> {
        dependsOn(prepareSubmodule)
    }

    val clean by getting(Delete::class) {
        delete(file("src/test/resources/sample"))
    }
}
