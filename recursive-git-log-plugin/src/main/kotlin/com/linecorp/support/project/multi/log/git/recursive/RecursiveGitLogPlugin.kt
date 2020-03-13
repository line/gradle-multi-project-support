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

package com.linecorp.support.project.multi.log.git.recursive

import org.gradle.api.*
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.process.ExecSpec
import java.io.ByteArrayOutputStream
import javax.inject.Inject

interface RecursiveGitLogParams {
    @get:Input
    val moduleNameTransformer: (Project) -> String

    @get:Input
    val logClassifiers: Map<String, (Project) -> Boolean>

    @get:Input
    val tagPattern: String

    @get:Input
    val trackFilePatterns: List<String>

    @get:Input
    val logPattern: String
}

open class RecursiveGitLogExtension : RecursiveGitLogParams {
    companion object {
        const val EXTENSION_NAME = "recursive-git-log-extension"
    }

    override var moduleNameTransformer: (Project) -> String = { it.toString() }
    override var logClassifiers = mutableMapOf<String, (Project) -> Boolean>()
    override var tagPattern = "v*"
    override var trackFilePatterns = mutableListOf("build.gradl*")
    override var logPattern = "%s\n Assignee: @%an\n Reviewed-by: @%cn\n"
}

enum class OutputType {
    STDOUT,
}

interface GitAffectedModuleParams {
    @get:Input
    val moduleNameTransformer: (Project) -> String

    @get:Input
    val affectedModuleFilter: (Project) -> Boolean

    @get:Input
    val tagPattern: String

    @get:Input
    val trackFilePatterns: List<String>

    @get:Input
    val output: OutputType
}

open class GitAffectedModuleExtension : GitAffectedModuleParams {
    companion object {
        const val EXTENSION_NAME = "git-affected-modules-extension"
    }

    override var moduleNameTransformer: (Project) -> String = { it.toString() }
    override var affectedModuleFilter: (Project) -> Boolean = { true }
    override var tagPattern = "v*"
    override val trackFilePatterns: List<String> = mutableListOf()
    override val output = OutputType.STDOUT
}

open class RecursiveGitLogPlugin : Plugin<Project> {
    companion object {
        const val GROUP = "changeLog"
        const val FROM_KEY = "log.git.from"
        const val TO_KEY = "log.git.to"
    }

    override fun apply(target: Project): Unit = target.run {
        val recursiveGitLogExtension = extensions.create(
                RecursiveGitLogExtension.EXTENSION_NAME,
                RecursiveGitLogExtension::class
        )

        val gitAffectedModuleExtension = extensions.create(
                GitAffectedModuleExtension.EXTENSION_NAME,
                GitAffectedModuleExtension::class
        )

        tasks {
            register(
                    "gitLog", GitLogTask::class,
                    recursiveGitLogExtension,
                    (findProperty(FROM_KEY) as String?) ?: "",
                    (findProperty(TO_KEY) as String?) ?: ""
            )

            register(
                    "gitAffectedModules", GitAffectedModuleTask::class,
                    gitAffectedModuleExtension,
                    (findProperty(FROM_KEY) as String?) ?: "",
                    (findProperty(TO_KEY) as String?) ?: ""
            )
        }
    }
}

open class GitAwareTask : DefaultTask() {
    private fun commandLine(exec: Action<in ExecSpec>) =
            ByteArrayOutputStream().use { os ->
                project.exec {
                    exec(this)
                    standardOutput = os
                }
                os.toString().trim()
            }

    private fun commandLine(args: List<String>) = commandLine { commandLine(args.toMutableList()) }
    private fun commandLine(vararg args: String) = commandLine { commandLine(*args) }

    fun gitCommand(vararg args: String) = commandLine("git", *args)

    fun getLastTag(tagPattern: String? = null): String {
        val lastTagCommitHash = gitCommand(
                "rev-list",
                tagPattern?.let { "--tags=$it" } ?: "--tags",
                "--max-count=1"
        )

        if (lastTagCommitHash.isBlank()) {
            throw GradleException("Can't find previous tag.")
        }

        return try {
            gitCommand("describe", "--tags", lastTagCommitHash)
        } catch (e: Exception) {
            throw GradleException(e.message!!, e)
        }
    }
}

private val resolutionCache = mutableMapOf<Project, Set<Project>>()

private fun resolveDependencies(project: Project): Set<Project> {
    if (resolutionCache.containsKey(project)) {
        return resolutionCache[project]!!
    }

    return (project
            .configurations
            .asMap
            .values
            .asSequence()
            .map { it.allDependencies.withType(ProjectDependency::class) }
            .flatten()
            .distinct()
            .onEach { project.logger.info("[{}] has project dependency [{}]", project, it.dependencyProject) }
            .map { resolveDependencies(it.dependencyProject) }
            .flatten()
            .distinct()
            .toMutableSet()
            .takeIf { it.isNotEmpty() }
            ?.also { it.add(project) }
            ?: setOf(project))
            .also {
                project.logger.info(
                        "resolved dependency dirs for {} is [\n {}\n]",
                        project,
                        it.joinToString("\n ") { p -> p.projectDir.toString() }
                )
            }.also { resolutionCache[project] = it }
}

open class GitLogTask @Inject constructor(
        param: RecursiveGitLogParams,
        @Input val from: String,
        @Input val to: String
) : GitAwareTask(), RecursiveGitLogParams by param {
    @Internal
    override fun getGroup() = RecursiveGitLogPlugin.GROUP

    @Internal
    override fun getDescription() = """
                Produce logs per module affected between two specific points.
                -Plog.git.from=<from:latestTag>
                -Plog.git.to=<to:HEAD>
            """.trimIndent()

    @OutputDirectory
    val outputDir = project.buildDir


    private fun diffFileName(from: String, to: String) =
            "change_log_${from}_${to.takeUnless(String::isBlank) ?: "head"}"


    private fun Sequence<Project>.toGitLog(from: String, to: String = ""): String {
        return map {
            moduleNameTransformer(it) to gitCommand(
                    "log",
                    "--pretty=${logPattern}",
                    "$from..$to",
                    it.projectDir.absolutePath,
                    *trackFilePatterns.toTypedArray(),
                    *resolveDependencies(it).map { p -> p.projectDir.absolutePath }.toTypedArray()
            )
        }.filter { it.second.isNotBlank() }
                .joinToString(separator = "\n\n---\n\n") { "[${it.first}]\n\n${it.second}" }
    }

    @TaskAction
    fun act() {
        val from = this.from.takeIf(String::isNotBlank) ?: getLastTag(tagPattern)
        val to = this.to
        val diffFileName = diffFileName(from, to)

        if (logClassifiers.isNotEmpty()) {
            logClassifiers
                    .mapValues { project.subprojects.asSequence().filter(it.value::invoke).toGitLog(from, to) }
                    .forEach { (classifier, log) ->
                        project.file("$outputDir/${diffFileName}_$classifier.log").writeText(log)
                    }
        } else {
            project.subprojects
                    .asSequence()
                    .toGitLog(from, to)
                    .also { project.file("$outputDir/$diffFileName.log").writeText(it) }
        }
    }
}

open class GitAffectedModuleTask @Inject constructor(
        param: GitAffectedModuleParams,
        @Input val from: String,
        @Input val to: String
) : GitAwareTask(), GitAffectedModuleParams by param {
    @Internal
    override fun getGroup() = RecursiveGitLogPlugin.GROUP

    @Internal
    override fun getDescription() = """
                Produce affected modules between two specific points.
                -Plog.git.from=<from:latestTag>
                -Plog.git.to=<to:HEAD>
            """.trimIndent()

    @OutputDirectory
    val outputDir = project.buildDir

    private fun Sequence<Project>.findAffectedModules(from: String, to: String = ""): Sequence<Project> {
        return filter {
            gitCommand(
                    "log",
                    "$from..$to",
                    it.projectDir.absolutePath,
                    *resolveDependencies(it).map { p -> p.projectDir.absolutePath }.toTypedArray()
            ).isNotBlank()
        }
    }

    @TaskAction
    fun act() {
        val from = this.from.takeIf(String::isNotBlank) ?: getLastTag(tagPattern)
        val to = this.to

        project.allprojects
                .asSequence()
                .filter { it != project.rootProject }
                .findAffectedModules(from, to)
                .filter(affectedModuleFilter)
                .map(moduleNameTransformer)
                .run {
                    when (output) {
                        OutputType.STDOUT -> forEach { println(it) }
                    }
                }
    }
}
