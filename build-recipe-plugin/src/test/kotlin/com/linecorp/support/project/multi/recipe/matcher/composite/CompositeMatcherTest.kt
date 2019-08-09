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

package com.linecorp.support.project.multi.recipe.matcher.composite

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.linecorp.support.project.multi.recipe.ProjectMatcher
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.not
import com.linecorp.support.project.multi.recipe.matcher.and
import com.linecorp.support.project.multi.recipe.matcher.or
import io.mockk.mockk
import org.gradle.api.Project
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class CompositeMatcherTest {

    private val project = mockk<Project>()

    companion object {
        private val `true` = ProjectMatcher { true }
        private val `false` = ProjectMatcher { false }

        @JvmStatic
        fun andArgsProvider() = Stream.of(
                arguments(
                        `true`, `true`,
                        true
                ),
                arguments(
                        `true`, `false`,
                        false
                ),
                arguments(
                        `false`, `true`,
                        false
                ),
                arguments(
                        `false`, `false`,
                        false
                )
        )

        @JvmStatic
        fun orArgsProvider() = Stream.of(
                arguments(
                        `true`, `true`,
                        true
                ),
                arguments(
                        `true`, `false`,
                        true
                ),
                arguments(
                        `false`, `true`,
                        true
                ),
                arguments(
                        `false`, `false`,
                        false
                )
        )

        @JvmStatic
        fun notArgsProvider() = Stream.of(
                arguments(`true`, false),
                arguments(`false`, true)
        )
    }

    @ParameterizedTest
    @MethodSource("andArgsProvider")
    fun `test and`(first: ProjectMatcher, second: ProjectMatcher, expected: Boolean) {
        assertThat((first and second).test(project)).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("orArgsProvider")
    fun `test or`(first: ProjectMatcher, second: ProjectMatcher, expected: Boolean) {
        assertThat((first or second).test(project)).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("notArgsProvider")
    fun `test not`(condition: ProjectMatcher, expected: Boolean) {
        assertThat(not(condition).test(project)).isEqualTo(expected)
    }
}
