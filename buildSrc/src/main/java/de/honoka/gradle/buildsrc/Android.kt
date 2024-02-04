package de.honoka.gradle.buildsrc

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get

fun Project.defineSourceJarTask() {
    val project = this
    tasks.register("sourceJar", Jar::class.java) {
        val sourceSet = project.android.sourceSets["main"].java
        val srcDir =  sourceSet.javaClass.getDeclaredMethod("getSrcDirs").invoke(sourceSet)
        from(srcDir)
        archiveClassifier.set("sources")
    }
}