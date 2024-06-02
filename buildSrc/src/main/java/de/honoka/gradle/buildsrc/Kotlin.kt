package de.honoka.gradle.buildsrc

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.catalog.VersionModel
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.dependencies

fun DependencyHandler.kotlin(project: Project) {
    val versions: Map<String, VersionModel> = project.libVersions()
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.getVersion("kotlin")}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${versions.getVersion("kotlin")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.getVersion("kotlin.coroutines")}")
}