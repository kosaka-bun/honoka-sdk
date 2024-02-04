package de.honoka.gradle.buildsrc

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

val Project.android: LibraryExtension
    get() = extensions.getByName("android") as LibraryExtension

val NamedDomainObjectContainer<Configuration>.implementation: NamedDomainObjectProvider<Configuration>
    get() = named<Configuration>("implementation")

fun Project.publishing(configure: Action<PublishingExtension>) {
    extensions.configure("publishing", configure)
}

fun Project.android(configure: Action<LibraryExtension>) {
    extensions.configure("android", configure)
}

fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? {
    return add("androidTestImplementation", dependencyNotation)
}

fun LibraryExtension.kotlinOptions(configure: Action<KotlinJvmOptions>) {
    this as ExtensionAware
    extensions.configure("kotlinOptions", configure)
}
