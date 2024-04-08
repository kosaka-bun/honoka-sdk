package de.honoka.gradle.buildsrc

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

object MavenPublish {

    private lateinit var rootProject: Project

    private val projectsWillPublish = ArrayList<Project>()

    fun Project.setupVersionAndPublishing(version: String) {
        val project = this
        this.version = version
        publishing {
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

    private fun checkVersionOfProjects() {
        var passed = true
        println("Versions:\n")
        listOf(rootProject, *projectsWillPublish.toTypedArray()).forEach {
            if(!passed) return@forEach
            //若project未设置version，则这里取到的version值为unspecified
            println("${it.name}=${it.version}")
            passed = it.version.toString().lowercase().run {
                !(isEmpty() || this == "unspecified" || contains("dev"))
            }
        }
        println("\nResults:\n")
        println("results.passed=$passed")
        println()
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
}