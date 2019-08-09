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

package com.linecorp.support.project.multi.recipe.matcher.type

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.linecorp.support.project.multi.recipe.ProjectMatcher
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byType
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypeHaving
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypePrefix
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypeRegex
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypeSuffix
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class ProjectTypeMatcherTest {

    companion object {
        private val project = mockk<Project>()
        private const val type = "java-boot-application"
        private const val prefix = "java"
        private const val suffix = "application"

        @JvmStatic
        fun argumentProvider() = Stream.of(
                arguments(byType(type), true),
                arguments(byType(prefix), false),
                arguments(byTypePrefix(prefix), true),
                arguments(byTypePrefix(suffix), false),
                arguments(byTypeSuffix(suffix), true),
                arguments(byTypeSuffix(prefix), false),
                arguments(byTypeRegex("java-.*"), true),
                arguments(byTypeRegex("java-*"), false),
                arguments(byTypeRegex(Regex("java-.*")), true),
                arguments(byTypeRegex(Regex("java-*")), false),
                arguments(byTypeHaving("java", "boot"), true),
                arguments(byTypeHaving("java", "application"), true),
                arguments(byTypeHaving("boot", "application"), true),
                arguments(byTypeHaving("kotlin", "application"), false)
        )
    }

    @BeforeEach
    internal fun setUp() {
        every {
            project.findProperty(PROJECT_TYPE_PROPERTY)
        } returns (type)
    }

    @ParameterizedTest
    @MethodSource("argumentProvider")
    fun test(matcher: ProjectMatcher, expected: Boolean) {
        assertThat(matcher.test(project)).isEqualTo(expected)
    }
}
