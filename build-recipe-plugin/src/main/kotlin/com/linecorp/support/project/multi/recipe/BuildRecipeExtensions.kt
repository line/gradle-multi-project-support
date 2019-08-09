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

import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byLabel
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byLabels
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byType
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypeHaving
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypePrefix
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypeRegex
import com.linecorp.support.project.multi.recipe.matcher.ProjectMatchers.Companion.byTypeSuffix
import com.linecorp.support.project.multi.recipe.matcher.label.LabelMultiMatcher
import com.linecorp.support.project.multi.recipe.matcher.type.*
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import java.util.function.Predicate

typealias ProjectMatcher = Predicate<Project>
typealias ProjectConfigurer = Action<Project>

fun Project.configureByType(type: String, configurer: ProjectConfigurer) =
        configure(byType(type), configurer)

fun Project.configureByTypePrefix(typePrefix: String, configurer: ProjectConfigurer) =
        configure(byTypePrefix(typePrefix), configurer)

fun Project.configureByTypeSuffix(typeSuffix: String, configurer: ProjectConfigurer) =
        configure(byTypeSuffix(typeSuffix), configurer)

fun Project.configureByTypeExpression(expr: String, configurer: ProjectConfigurer) =
        configure(byTypeRegex(expr), configurer)

fun Project.configureByTypeHaving(vararg typelets: String, configurer: ProjectConfigurer) =
        configure(byTypeHaving(*typelets), configurer)

fun Project.configureByLabel(label: String, configurer: ProjectConfigurer) =
        configure(byLabel(label), configurer)

fun Project.configureByLabels(vararg labels: String, configurer: ProjectConfigurer) =
        configure(byLabels(*labels), configurer)

fun Project.configure(projectMatcher: ProjectMatcher, configurer: ProjectConfigurer) {
    configure(
            allprojects
                    .filter(projectMatcher::test)
                    .onEach {
                        (it.extensions[BUILD_RECIPE_EXTENSION_NAME] as BuildRecipePluginExtension)
                                .logger
                                .add(projectMatcher to configurer)
                    },
            configurer
    )
}

fun Project.isSameTypeOf(type: String) = ProjectTypeExactMatcher(type).test(this)

fun Project.hasTypePrefix(typePrefix: String) = ProjectTypePrefixMatcher(typePrefix).test(this)

fun Project.hasTypeSuffix(typeSuffix: String) = ProjectTypeSuffixMatcher(typeSuffix).test(this)

fun Project.matchesWithTypeExpression(pattern: String) = ProjectTypeRegexMatcher(Regex(pattern)).test(this)

fun Project.havingType(vararg typelet: String) = ProjectTypeHavingMatcher(typelet.toList()).test(this)

fun Project.hasLabels(vararg label: String) = LabelMultiMatcher(label.toList()).test(this)
