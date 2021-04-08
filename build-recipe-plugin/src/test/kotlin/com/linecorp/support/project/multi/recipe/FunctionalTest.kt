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

import assertk.assertThat
import assertk.assertions.contains
import org.gradle.kotlin.dsl.support.normaliseLineSeparators
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Paths

class FunctionalTest {

    private val expected = """
> Configure project :
project ':coffee:api:client' is configured by type prefix java
project ':coffee:api:protocol' is configured by type prefix java
project ':coffee:api:server' is configured by type prefix java
project ':juice:api:client' is configured by type prefix java
project ':juice:api:protocol' is configured by type prefix java
project ':juice:api:server' is configured by type prefix java
project ':coffee:api:client' is configured by type prefix java and suffix lib
project ':coffee:api:protocol' is configured by type prefix java and suffix lib
project ':juice:api:client' is configured by type prefix java and suffix lib
project ':juice:api:protocol' is configured by type prefix java and suffix lib
project ':coffee:api:client' is configured by type having boot
project ':coffee:api:server' is configured by type having boot
project ':juice:api:client' is configured by type having boot
project ':juice:api:server' is configured by type having boot
project ':coffee:api:client' is configured by type suffix boot-lib
project ':juice:api:client' is configured by type suffix boot-lib
project ':coffee:api:server' is configured by type suffix boot-application
project ':juice:api:server' is configured by type suffix boot-application

> Task :allProjectReport
project ':coffee:api:client' is configured by 
 - type prefix [java]
 - and(type prefix [java], type suffix [lib])
 - type having [boot]
 - type suffix [boot-lib]
project ':coffee:api:protocol' is configured by 
 - type prefix [java]
 - and(type prefix [java], type suffix [lib])
project ':coffee:api:server' is configured by 
 - type prefix [java]
 - type having [boot]
 - type suffix [boot-application]
project ':juice:api:client' is configured by 
 - type prefix [java]
 - and(type prefix [java], type suffix [lib])
 - type having [boot]
 - type suffix [boot-lib]
project ':juice:api:protocol' is configured by 
 - type prefix [java]
 - and(type prefix [java], type suffix [lib])
project ':juice:api:server' is configured by 
 - type prefix [java]
 - type having [boot]
 - type suffix [boot-application]""".trimIndent().normaliseLineSeparators()

    @ParameterizedTest
    @ValueSource(strings = ["kotlin-dsl", "groovy-dsl"])
    fun `dsl test`(dslResource: String) {
        val file = Paths.get(this::class.java.classLoader.getResource(dslResource)!!.toURI())

        GradleRunner.create()
                .withProjectDir(file.toFile())
                .withPluginClasspath()
                .withArguments("allProjectReport")
                .build()
                .also { println(it.output) }
                .let { it.output.normaliseLineSeparators() }
                .also { assertThat(it).contains(expected) }
    }
}
