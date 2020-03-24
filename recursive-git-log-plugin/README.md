# Recursive Git Log Plugin

[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com/linecorp/recursive-git-log-plugin/com.linecorp.recursive-git-log-plugin.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=Gradle%20Plugin%20Portal)](https://plugins.gradle.org/plugin/com.linecorp.recursive-git-log-plugin)

A Gradle plugin to extract the changelog of the affected project via git history log, for large scale Gradle multi-project.

## Motivation

When we use a mono-repo, it was hard to track affected changes when we modify some modules.

We could check the root git log to find out changes, of course. But if so, do we have to deploy all the applications even if it does not have any changes?

The solution was `recursive-git-log-plugin`.

## Getting started

### Prerequisites

This has been tested on

- [Java 8 or later](https://adoptopenjdk.net/)
- [Gradle 5 or later](https://gradle.org/releases/)

### Appliance

Please follow [Gradle Plugins Portal](https://plugins.gradle.org/plugin/com.linecorp.recursive-git-log-plugin)'s guidance.

and type

`./gradlew -Plog.git.from=v4.2.39 -Plog.git.to=v4.2.40 gitLog`

This will produce change logs under the `build` directory.

## Limitations

This can assure inter-project dependencies through **current code status**.

So please note that it may be different from actual.

## Configurations

```kotlin
import com.linecorp.support.project.multi.log.git.recursive.RecursiveGitLogPluginExtension

configure<RecursiveGitLogPluginExtention> {
    // Way to support separated change log by some constraints 
    logClassifiers = mutableMapOf(
        "application" to { it.name.endsWith("application") },
        "modules" to { it.name.endsWith("lib") }
    )
    // If you wanna decorate your project name in git log, use this.
    moduleNameTransformer = { it.toString() }
    // Tag pattern to find
    tagPattern = "v*"
    // Files will be tracked together 
    trackFilePatterns = mutableListOf("build.gradl*")
    // Git log pattern
    logPattern = "%s\n Assignee: @%an\n Reviewed-by: @%cn\n"
}
```
