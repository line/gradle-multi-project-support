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

import com.gradle.publish.PluginBundleExtension
import com.linecorp.support.project.multi.recipe.configure
import com.linecorp.support.project.multi.recipe.configureByType
import com.linecorp.support.project.multi.recipe.configureByTypePrefix
import com.linecorp.support.project.multi.recipe.configureByTypeSuffix
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byLabel
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypeSuffix
import com.linecorp.support.project.multi.recipe.matcher.and
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.gradle.kotlin:plugins:1.4.9")
    }
}

plugins {
    idea
    eclipse
    `visual-studio`
    xcode
    kotlin("jvm") version embeddedKotlinVersion
    id("com.gradle.plugin-publish").version("0.10.1").apply(false)
    id("org.jmailen.kotlinter") version "2.1.2"
    id("com.linecorp.build-recipe-plugin") version "1.0.0"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

configureByTypePrefix("kotlin") {
    apply(plugin = "kotlin")

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

configureByType("kotlin-gradle-plugin") {
    apply(plugin = "org.gradle.kotlin.kotlin-dsl")
    apply(plugin = "org.gradle.java-gradle-plugin")
    apply(plugin = "com.gradle.plugin-publish")

    configure<GradlePluginDevelopmentExtension> {
        dependencies {
            testImplementation(gradleTestKit())
            testImplementation(gradleApi())
            testImplementation(gradleKotlinDsl())
            testImplementation(kotlin("stdlib"))
        }
    }
}

configureByTypeSuffix("gradle-plugin") {
    configure<PluginBundleExtension> {
        website = "https://github.com/line/multi-project-support"
        vcsUrl = "https://github.com/line/multi-project-support"
    }
}

configure(byTypeSuffix("gradle-plugin") and byLabel("junit5-platform")) {
    dependencies {
        testImplementation(kotlin("test-junit5"))
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.1")

        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")
        testImplementation("io.mockk:mockk:1.9.3")
        testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.21")
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}
