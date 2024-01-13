package de.honoka.gradle.buildsrc

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

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
}