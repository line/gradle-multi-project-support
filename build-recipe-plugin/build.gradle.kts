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
        register("buildRecipePlugin") {
            id = "com.linecorp.build-recipe-plugin"
            group = "com.linecorp.support.project.multi"
            implementationClass = "com.linecorp.support.project.multi.recipe.BuildRecipePlugin"
        }
    }

    dependencies {
        testImplementation("io.mockk:mockk:1.8.13")
        testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.19")
    }
}

pluginBundle {
    description = "Simple DSL plugin for managing large scale multi-application multi-project in mono-repo"

    (plugins) {
        "buildRecipePlugin" {
            displayName = "Build Recipe Plugin"
            tags = listOf("multi-project", "multi-application", "recipe", "DSL")
        }
    }
}
