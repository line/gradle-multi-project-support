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

package com.linecorp.support.project.multi.recipe.matcher

import com.linecorp.support.project.multi.recipe.ProjectMatcher
import com.linecorp.support.project.multi.recipe.matcher.composite.AndMatcher
import com.linecorp.support.project.multi.recipe.matcher.composite.NotMatcher
import com.linecorp.support.project.multi.recipe.matcher.composite.OrMatcher
import com.linecorp.support.project.multi.recipe.matcher.label.LabelMultiMatcher
import com.linecorp.support.project.multi.recipe.matcher.label.LabelSingleMatcher
import com.linecorp.support.project.multi.recipe.matcher.type.*

interface ProjectMatchers {
    companion object {
        @JvmStatic
        fun byType(type: String) = ProjectTypeExactMatcher(type)

        @JvmStatic
        fun byTypePrefix(typePrefix: String) = ProjectTypePrefixMatcher(typePrefix)

        @JvmStatic
        fun byTypeSuffix(typeSuffix: String) = ProjectTypeSuffixMatcher(typeSuffix)

        @JvmStatic
        fun byTypeRegex(regex: Regex) = ProjectTypeRegexMatcher(regex)

        @JvmStatic
        fun byTypeRegex(pattern: String) = ProjectTypeRegexMatcher(Regex(pattern))

        @JvmStatic
        fun byTypeHaving(vararg typelets: String) = ProjectTypeHavingMatcher(typelets.toList())

        @JvmStatic
        fun byLabel(label: String) = LabelSingleMatcher(label)

        @JvmStatic
        fun byLabels(vararg labels: String) = LabelMultiMatcher(labels.toList())

        @JvmStatic
        fun and(vararg matchers: ProjectMatcher) = AndMatcher(matchers.toList())

        @JvmStatic
        fun or(vararg matchers: ProjectMatcher) = OrMatcher(matchers.toList())

        @JvmStatic
        fun not(matcher: ProjectMatcher) = NotMatcher(matcher)
    }
}

infix fun ProjectMatcher.and(projectMatcher: ProjectMatcher) = ProjectMatchers.and(this, projectMatcher)

infix fun ProjectMatcher.or(projectMatcher: ProjectMatcher) = ProjectMatchers.or(this, projectMatcher)
