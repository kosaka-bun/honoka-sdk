package de.honoka.gradle.buildsrc

import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyHandler
import org.gradle.api.internal.catalog.VersionModel

@Suppress("UNCHECKED_CAST")
fun Project.libVersions(): Map<String, VersionModel> {
    val libs = extensions.getByName("libs")
    val versions = libs.javaClass.getDeclaredMethod("getVersions").invoke(libs)
    val catalog = versions.javaClass.superclass.getDeclaredField("config").run {
        isAccessible = true
        get(versions)
    }
    catalog.javaClass.getDeclaredField("versions").run {
        isAccessible = true
        return get(catalog) as Map<String, VersionModel>
    }
}

fun Map<String, VersionModel>.getVersion(key: String): String = get(key)?.version.toString()

val Project.rawDependencies: Set<Dependency>
    get() {
        val configurationContainerField = DefaultDependencyHandler::class.java.getDeclaredField("configurationContainer")
        val configurationContainer = configurationContainerField.run {
            isAccessible = true
            get(dependencies) as ConfigurationContainer
        }
        val set = HashSet<Dependency>()
        configurationContainer.forEach {
            it.dependencies.forEach {
                set.add(it as Dependency)
            }
        }
        return set
    }