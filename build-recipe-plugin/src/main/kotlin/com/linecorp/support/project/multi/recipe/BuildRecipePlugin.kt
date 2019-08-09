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

package com.linecorp.support.project.multi.recipe

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register

open class BuildRecipePluginExtension {
    val logger = mutableListOf<Pair<ProjectMatcher, ProjectConfigurer>>()
}

const val BUILD_RECIPE_EXTENSION_NAME = "build-recipe-plugin-extension"
const val RECIPE_GROUP = "Recipe"

open class BuildRecipePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        allprojects {
            val ext = extensions.create(BUILD_RECIPE_EXTENSION_NAME, BuildRecipePluginExtension::class)

            tasks {
                register("projectReport", ProjectReportTask::class) {
                    group = RECIPE_GROUP
                    log = ext.logger
                }
            }
        }

        tasks {
            register("allProjectReport", AllProjectReportTask::class) {
                group = RECIPE_GROUP
                logs = allprojects.map { it to it.extensions[BUILD_RECIPE_EXTENSION_NAME] as BuildRecipePluginExtension }
            }
        }
    }
}

open class ProjectReportTask : DefaultTask() {
    @Input
    lateinit var log: List<Pair<ProjectMatcher, ProjectConfigurer>>

    @TaskAction
    fun act() {
        log.takeIf { it.isNotEmpty() }
                ?.joinToString(separator = "\n  ", prefix = "\n[\n  ", postfix = "\n]") { it.first.toString() }
                ?.also { logger.lifecycle("$project is configured by $it") }

    }
}

open class AllProjectReportTask : DefaultTask() {
    @Input
    lateinit var logs: List<Pair<Project, BuildRecipePluginExtension>>

    @TaskAction
    fun act() {
        logs
                .filter { it.second.logger.isNotEmpty() }
                .map { (project, ext) ->
                    "$project is configured by \n${ext.logger.joinToString(separator = "\n") { " - ${it.first}" }}"
                }
                .onEach { logger.lifecycle(it) }
    }
}
