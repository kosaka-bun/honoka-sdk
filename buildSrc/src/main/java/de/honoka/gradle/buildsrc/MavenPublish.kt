package de.honoka.gradle.buildsrc

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

object MavenPublish {

    private val projectsWillPublish = ArrayList<Project>()

    private fun Project.publishing(configure: Action<PublishingExtension>) {
        extensions.configure("publishing", configure)
    }

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

    fun checkVersionOfProjects() {
        var passed = true
        println("Versions:\n")
        projectsWillPublish.forEach {
            if(!passed) return@forEach
            //若project未设置version，则这里取到的version值为unspecified
            println("${it.name}=${it.version}")
            passed = it.version.toString().toLowerCase().run {
                !(isEmpty() || this == "unspecified" || contains("dev"))
            }
        }
        println("\nResults:\n")
        println("results.passed=$passed")
        println()
    }
}