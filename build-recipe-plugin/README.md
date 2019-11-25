# Build Recipe Plugin

A Gradle plugin which makes your build configuration **reusable** and **centralized**.

We were strongly inspired by [Armeria's build scripts](https://github.com/line/armeria/tree/master/gradle/scripts).

## Getting started

### Prerequisites

This has been tested on

- [Java 8 or later](https://adoptopenjdk.net/)
- [Gradle 5 or later](https://gradle.org/releases/)

### Appliance

Please follow [Gradle Plugins Portal](https://plugins.gradle.org/plugin/com.linecorp.build-recipe-plugin)'s guidance.

## Basic concept

Let's suppose that a Gradle multi-project has similar modules.

```
├── coffee
│   └── api
│       ├── client        - type=java-boot-lib
│       ├── protocol      - type=java-lib
│       └── server        - type=java-boot-application
└── juice
    └── api
        ├── client        - type=java-boot-lib
        ├── protocol      - type=java-lib
        └── server        - type=java-boot-application
```
Then by using `gradle-recipe-plugin`, you can configure as below. 

```kotlin
import com.linecorp.support.project.multi.recipe.*

configure(byTypePrefix("java")) {
    apply(plugin="java")
}

configure(byTypePrefix("java") and byTypeSuffix("lib")) {
    apply(plugin="java-library")
}

configure(byTypePrefix("java-boot")) {
    apply(plugin = "org.springframework.boot")
}
```

Each configure function holds its domain dedicated configurations only. So the configuration blocks would be reusable, and that helps to remove duplicated build configuration between your projects.

- See the example of Kotlin DSL [here](./src/test/resources/kotlin-dsl/build.gradle.kts).

- See the example of Groovy DSL here [here](./src/test/resources/groovy-dsl/build.gradle).

## Interface function

Inspired from [Project.configure()](https://github.com/gradle/gradle/blob/master/subprojects/core-api/src/main/java/org/gradle/api/Project.java#L1469-L1477)
```java
    /**
     * Configures a collection of objects via a closure. This is equivalent to calling {@link #configure(Object,
     * groovy.lang.Closure)} for each of the given objects.
     *
     * @param objects The objects to configure
     * @param configureClosure The closure with configure statements
     * @return The configured objects.
     */
    Iterable<?> configure(Iterable<?> objects, Closure configureClosure)
```

This plugin provides a Kotlin extension method
```kotlin
fun Project.configure(projectMatcher: ProjectMatcher, configurer: ProjectConfigurer) {...}
```

### in Kotlin DSL

```kotlin
configure(byType("type")) {...}
configureByType("type") {...} //shorthanded extension function
```

### In Groovy DSL

```groovy
BuildRecipeExtensionKt.configureByType(project, "type") {
    // You can access target project via `it`
}

// Can be a shorthanded form inside use block
use(BuildRecipeExtensionKt) {
    configureByType("type") {    
        // You can access target project via `it`
    }
}
```

## Supported Filters

These filter keys should be declared in the `gradle.properties`.

### Type Filter

The filter key is `type`

Assumed to be *hyphen-separated string value* which is used for the *hierarchical struct projects*.

For example,

```
\
type=java-lib \
type=java-boot-lib \
type=java-boot-application
```

### Label Filter

The filter key is `label`

Assumed to be *comma separated string value* which is used for *non-hierarchical aspect of configurations*.

For example,

```
\
label=dockerized,deploy-aws,deploy-gcp
```

### Logical Filter

This is for composite usage of simple filters.

and, or, not is prepared

## Configuration Report

But for simple debugging, we have prepared a task which reports you to show the configuration applied 

for each projects

`$./gradlew projectReport`

or 

for all projects

`$./gradlew allProjectReport`

