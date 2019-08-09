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

import com.linecorp.support.project.multi.recipe.ProjectMatcher
import org.gradle.api.Project

interface CompositeProjectMatcher : ProjectMatcher {
    val matchers: List<ProjectMatcher>

    fun testMatchedResult(matchedResult: List<Boolean>): Boolean

    override fun test(t: Project): Boolean {
        return testMatchedResult(matchers.map { it.test(t) })
    }
}

data class AndMatcher(override val matchers: List<ProjectMatcher>) :
        CompositeProjectMatcher {
    override fun testMatchedResult(matchedResult: List<Boolean>) = matchedResult.all { it }

    override fun toString() = "and(${matchers.joinToString(", ")})"
}

data class OrMatcher(override val matchers: List<ProjectMatcher>) :
        CompositeProjectMatcher {
    override fun testMatchedResult(matchedResult: List<Boolean>) = matchedResult.any { it }

    override fun toString() = "or(${matchers.joinToString(", ")})"
}

data class NotMatcher(private val matcher: ProjectMatcher) :
        ProjectMatcher {
    override fun test(t: Project) = !matcher.test(t)

    override fun toString() = "not($matcher)"
}
