package de.honoka.gradle.buildsrc

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.catalog.VersionModel

fun DependencyHandler.kotlin(project: Project) {
    val versions: Map<String, VersionModel> = project.libVersions()
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.getVersion("kotlin")}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${versions.getVersion("kotlin")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.getVersion("kotlin.coroutines")}")
}

fun DependencyHandler.implementationApi(dn: Any): Dependency? = run {
    implementation(dn)
    api(dn)
}
