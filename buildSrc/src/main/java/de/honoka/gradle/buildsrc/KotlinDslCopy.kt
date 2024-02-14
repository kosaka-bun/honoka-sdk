package de.honoka.gradle.buildsrc

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.named

fun Project.publishing(configure: Action<PublishingExtension>) {
    extensions.configure("publishing", configure)
}

fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? = run {
    add("implementation", dependencyNotation)
}