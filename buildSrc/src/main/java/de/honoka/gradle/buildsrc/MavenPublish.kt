package de.honoka.gradle.buildsrc

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

object MavenPublish {

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

    fun Project.setupAndroidAarVersionAndPublishing(version: String) {
        val project = this
        this.version = version
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    groupId = group as String
                    artifactId = project.name
                    this.version = version
                    pom.setAndroidAarPomDependencies(project)
                    afterEvaluate {
                        val artifacts = listOf(
                            tasks["bundleReleaseAar"],
                            tasks["releaseSourcesJar"]
                        )
                        setArtifacts(artifacts)
                    }
                }
            }
        }
        projectsWillPublish.add(this)
    }

    private fun MavenPom.setAndroidAarPomDependencies(project: Project)  = withXml {
        val apiDependencies = ArrayList<String>()
        project.configurations["api"].allDependencies.forEach {
            apiDependencies.add("${it.group}:${it.name}")
        }
        asNode().appendNode("dependencies").run {
            project.configurations.implementation.configure {
                allDependencies.forEach {
                    val isInvalidDependency = it.group == null ||
                        it.name.lowercase() == "unspecified" ||
                        it.version == null
                    if(isInvalidDependency) return@forEach
                    val moduleName = "${it.group}:${it.name}"
                    appendNode("dependency").run {
                        val subNodes = hashMapOf(
                            "groupId" to it.group,
                            "artifactId" to it.name,
                            "version" to it.version
                        )
                        if(!apiDependencies.contains(moduleName)) {
                            subNodes["scope"] = "runtime"
                        }
                        subNodes.forEach { entry ->
                            appendNode(entry.key, entry.value)
                        }
                    }
                }
            }
        }
    }

    private fun checkVersionOfProjects() {
        var passed = true
        println("Versions:\n")
        projectsWillPublish.forEach {
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
        tasks.register("checkVersionOfProjects") {
            group = "publishing"
            doLast {
                checkVersionOfProjects()
            }
        }
    }
}