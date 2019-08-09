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

import com.linecorp.support.project.multi.recipe.*
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypePrefix
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypeSuffix
import com.linecorp.support.project.multi.recipe.matcher.*
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("com.linecorp.build-recipe-plugin")
    id("org.springframework.boot").version("2.1.7.RELEASE")
    java
    `java-library`
}

configureByTypePrefix("java") {
    apply(plugin = "java")
    println("$this is configured by type prefix java")
}

configure(byTypePrefix("java") and byTypeSuffix("lib")) {
    apply(plugin = "java-library")
    println("$this is configured by type prefix java and suffix lib")
}

configureByTypeHaving("boot") {
    apply(plugin = "org.springframework.boot")
    println("$this is configured by type having boot")
}

configureByTypeSuffix("boot-lib") {
    tasks {
        getByName<BootJar>("bootJar") {
            enabled = false
        }
    }

    println("$this is configured by type suffix boot-lib")
}

configureByTypeSuffix("boot-application") {
    tasks {
        getByName<BootJar>("bootJar") {
            enabled = true
        }
    }

    println("$this is configured by type suffix boot-application")
}
