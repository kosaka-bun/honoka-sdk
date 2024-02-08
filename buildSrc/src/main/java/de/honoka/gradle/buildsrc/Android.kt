package de.honoka.gradle.buildsrc

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.bundling.Zip
import kotlin.io.path.Path

fun Project.defineJarTask() {
    val project = this
    tasks.register("jar", Zip::class.java) {
        group = "build"
        dependsOn("bundleLibCompileToJarRelease")
        val jarPath = Path(
            project.buildDir.absolutePath,
            "./intermediates/compile_library_classes_jar/release/classes.jar"
        ).normalize().toFile()
        (archiveFile as RegularFileProperty).convention { jarPath }
        archiveExtension.set("jar")
    }
}