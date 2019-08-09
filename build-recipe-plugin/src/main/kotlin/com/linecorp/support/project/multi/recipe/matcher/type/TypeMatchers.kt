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

import com.linecorp.support.project.multi.recipe.ProjectMatcher
import org.gradle.api.Project

const val PROJECT_TYPE_PROPERTY = "type"
const val PROJECT_TYPE_DELIMITER = "-"

interface ProjectTypeMatcher : ProjectMatcher {
    fun testProjectType(type: String): Boolean

    override fun test(t: Project) = t.findProperty(PROJECT_TYPE_PROPERTY)
            ?.let { it as String }
            ?.let { testProjectType(it) }
            ?: false
}

data class ProjectTypeExactMatcher(private val type: String) :
        ProjectTypeMatcher {
    override fun testProjectType(type: String) = this.type == type

    override fun toString() = "type [$type]"
}

data class ProjectTypePrefixMatcher(private val typePrefix: String) :
        ProjectTypeMatcher {
    override fun testProjectType(type: String) = type.startsWith(typePrefix)

    override fun toString() = "type prefix [$typePrefix]"
}

data class ProjectTypeSuffixMatcher(private val typeSuffix: String) :
        ProjectTypeMatcher {
    override fun testProjectType(type: String) = type.endsWith(typeSuffix)

    override fun toString() = "type suffix [$typeSuffix]"
}

data class ProjectTypeHavingMatcher(private val typelets: List<String>) :
        ProjectTypeMatcher {
    override fun testProjectType(type: String) = type.split(PROJECT_TYPE_DELIMITER).containsAll(typelets)

    override fun toString() = "type having [${typelets.joinToString(", ")}]"
}

data class ProjectTypeRegexMatcher(private val regex: Regex) :
        ProjectTypeMatcher {
    override fun testProjectType(type: String) = regex.matches(type)

    override fun toString() = "type regex [${regex.pattern}]"
}
