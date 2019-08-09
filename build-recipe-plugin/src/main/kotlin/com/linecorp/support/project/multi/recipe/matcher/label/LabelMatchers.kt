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

import com.linecorp.support.project.multi.recipe.ProjectMatcher
import org.gradle.api.Project

const val PROJECT_LABEL_PROPERTY = "label"

interface LabelMatcher : ProjectMatcher {
    fun testLabelContains(labels: Set<String>): Boolean

    override fun test(t: Project) = t.findProperty(PROJECT_LABEL_PROPERTY)
            ?.let { it as String }
            ?.let { it.split(",") }
            ?.let { it.map(String::trim).filter(String::isNotBlank).toSet() }
            ?.let { testLabelContains(it) }
            ?: false
}

data class LabelSingleMatcher(private val label: String) :
        LabelMatcher {
    override fun testLabelContains(labels: Set<String>) =
            labels.contains(label)

    override fun toString() = "label [$label]"
}

data class LabelMultiMatcher(private val labels: List<String>) :
        LabelMatcher {
    override fun testLabelContains(labels: Set<String>) =
            labels.containsAll(this.labels)

    override fun toString() = "labels [${labels.joinToString(", ")}]"
}
