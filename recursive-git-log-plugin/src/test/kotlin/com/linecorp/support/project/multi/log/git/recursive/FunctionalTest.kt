/*
 * Copyright 2020 LINE Corporation
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

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import org.gradle.kotlin.dsl.support.normaliseLineSeparators
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class FunctionalTest {
    private val here = File("").absolutePath
    private val path = File("$here/src/test/resources/sample")

    @Test
    fun `gitLog test`() {
        val expectedFilePath = File("$here/src/test/resources/assertion")

        GradleRunner.create()
            .withProjectDir(path)
            .withPluginClasspath()
            .withArguments("clean")
            .build()

        GradleRunner.create()
            .withProjectDir(path)
            .withPluginClasspath()
            .withArguments("gitLog", "-Plog.git.from=v0.0.1", "-Plog.git.to=v0.0.2")
            .build()

        GradleRunner.create()
            .withProjectDir(path)
            .withPluginClasspath()
            .withArguments("gitLog", "-Plog.git.from=v0.0.2", "-Plog.git.to=v0.0.3")
            .build()

        GradleRunner.create()
            .withProjectDir(path)
            .withPluginClasspath()
            .withArguments("gitLog", "-Plog.git.from=v0.0.1", "-Plog.git.to=v0.0.3")
            .build()

        File("$path/build/change_log_v0.0.1_v0.0.2.log").assertSameContents("$expectedFilePath/change_log_v0.0.1_v0.0.2.log")
        File("$path/build/change_log_v0.0.2_v0.0.3.log").assertSameContents("$expectedFilePath/change_log_v0.0.2_v0.0.3.log")
        File("$path/build/change_log_v0.0.1_v0.0.3.log").assertSameContents("$expectedFilePath/change_log_v0.0.1_v0.0.3.log")
    }

    private fun File.assertSameContents(expected: String) {
        assertTrue(exists())
        val gitLog = readLines().joinToString("\n")

        val expectedLog = File(expected).readLines().joinToString("\n")

        assertThat(gitLog).isEqualTo(expectedLog)
    }

    @Test
    fun `gitAffectedModules test`() {

        GradleRunner.create()
                .withProjectDir(path)
                .withPluginClasspath()
                .withArguments("gitAffectedModules", "-Plog.git.from=v0.0.1", "-Plog.git.to=v0.0.2")
                .build()
                .also { assertThat(it.output.normaliseLineSeparators()).contains("""
                    project ':coffee'
                    project ':coffee:api'
                    project ':coffee:protocol'
                    project ':shop:server'
                    project ':coffee:api:client'
                    project ':coffee:api:server'
                """.trimIndent()) }

        GradleRunner.create()
                .withProjectDir(path)
                .withPluginClasspath()
                .withArguments("gitAffectedModules", "-Plog.git.from=v0.0.2", "-Plog.git.to=v0.0.3")
                .build()
                .also { assertThat(it.output.normaliseLineSeparators()).contains("""
                    project ':juice'
                    project ':juice:api'
                    project ':shop:server'
                    project ':juice:api:client'
                """.trimIndent()) }

        GradleRunner.create()
                .withProjectDir(path)
                .withPluginClasspath()
                .withArguments("gitAffectedModules", "-Plog.git.from=v0.0.1", "-Plog.git.to=v0.0.3")
                .build()
                .also { assertThat(it.output.normaliseLineSeparators()).contains("""
                    project ':coffee'
                    project ':juice'
                    project ':coffee:api'
                    project ':coffee:protocol'
                    project ':juice:api'
                    project ':shop:server'
                    project ':coffee:api:client'
                    project ':coffee:api:server'
                    project ':juice:api:client'
                """.trimIndent()) }
    }
}
