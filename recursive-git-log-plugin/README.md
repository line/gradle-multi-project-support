# Recursive Git Log Plugin

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

### Gradle Kotlin DSL

```kotlin
import com.linecorp.gradle.plugins.log.git.recursive.RecursiveGitLogPlugin

buildscript {
    repository {
        jcenter()
    }

    dependencies {
        classpath("com.linecorp.support.project.multi:recursive-git-log-plugin:$version")
    }
}

apply<RecursiveGitLogPlugin>()
```
or
``````
plugin {
    id("com.linecorp.recursive-git-log-plugin").version(version)
}
```

and type

`./gradlew -Plog.git.from=v4.2.39 -Plog.git.to=v4.2.40 gitLog`

This will produce change logs under the `build` directory.

## Limitations

This can assure inter-project dependencies through **current code status**.

So please note that it may be different from actual.

## Configurations

```kotlin
import com.linecorp.gradle.plugins.log.git.recursive.RecursiveGitLogPluginExtension

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

