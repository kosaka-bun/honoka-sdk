package de.honoka.gradle.buildsrc

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.maven

object MavenPublish {

    private lateinit var rootProject: Project

    private val projectsWillPublish = ArrayList<Project>()

    fun Project.setupVersionAndPublishing(version: String) {
        val project = this
        this.version = version
        publishing {
            repositories {
                val isReleaseVersion = version.isReleaseVersion()
                val isDevelopmentRepository = properties["isDevelopmentRepository"]?.toString() == "true"
                if(isReleaseVersion == isDevelopmentRepository) return@repositories
                val remoteUrl = properties["remoteMavenRepositoryUrl"]?.toString() ?: return@repositories
                maven(remoteUrl)
            }
            publications {
                create<MavenPublication>("maven") {
                    groupId = group as String
                    artifactId = project.name
                    this.version = version
                    from(components["java"])
                }
            }
        }
        projectsWillPublish.add(this)
    }

    fun Project.defineCheckVersionOfProjectsTask() {
        this@MavenPublish.rootProject = rootProject
        tasks.register("checkVersionOfProjects") {
            group = "publishing"
            doLast {
                checkVersionOfProjects()
            }
        }
    }

    private fun checkVersionOfProjects() {
        var passed = true
        val dependencies = HashSet<Dependency>()
        println("Versions:\n")
        listOf(rootProject, *projectsWillPublish.toTypedArray()).forEach {
            if(!passed) return@forEach
            //若project未设置version，则这里取到的version值为unspecified
            println("${it.name}=${it.version}")
            dependencies.addAll(it.rawDependencies)
            passed = it.version.isReleaseVersion()
        }
        if(passed) passed = checkVersionOfDependencies(dependencies)
        println("\nResults:\n")
        println("results.passed=$passed")
        println()
    }

    private fun Any?.isReleaseVersion(): Boolean = toString().lowercase().run {
        !(isEmpty() || this == "unspecified" || contains("dev"))
    }

    private fun checkVersionOfDependencies(dependencies: Set<Dependency>): Boolean {
        var passed = true
        println("\nDependencies:\n")
        dependencies.forEach {
            if(!passed) return@forEach
            println("${it.group}:${it.name}=${it.version}")
            passed = it.version.isReleaseVersion()
        }
        return passed
    }
}