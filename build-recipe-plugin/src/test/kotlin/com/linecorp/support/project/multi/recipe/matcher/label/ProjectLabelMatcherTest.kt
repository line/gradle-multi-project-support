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

package com.linecorp.support.project.multi.recipe.matcher.label

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byLabel
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byLabels
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.junit.jupiter.api.Test

class ProjectLabelMatcherTest {
    companion object {
        private val project = mockk<Project>()

    }

    @Test
    fun `single match`() {
        val label = "dockerized"
        every {
            project.findProperty(PROJECT_LABEL_PROPERTY)
        } returns label

        assertThat(byLabel(label).test(project))
                .isTrue()
    }

    @Test
    fun `multi value single match`() {
        val label1 = "dockerized"
        val label2 = "inhouse"
        val labels = "$label1, $label2"
        every {
            project.findProperty(PROJECT_LABEL_PROPERTY)
        } returns labels

        assertThat(byLabel(label1).test(project))
                .isTrue()

        assertThat(byLabel(label2).test(project))
                .isTrue()
    }

    @Test
    fun `multi value multi match`() {
        val label1 = "dockerized"
        val label2 = "inhouse"
        val labels = "$label1, $label2"
        every {
            project.findProperty(PROJECT_LABEL_PROPERTY)
        } returns labels

        assertThat(byLabels(label1, label2).test(project))
                .isTrue()

    }

    @Test
    fun `no label property`() {
        assertThat(byLabel("any").test(project))
                .isFalse()
    }
}
