import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val versions = de.honoka.gradle.buildsrc.Versions
    //plugins
    kotlin("jvm") version versions.kotlinVersion
}

setupVersionAndPublishing("1.0.0-dev")

dependencies {
    kotlin()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = java.sourceCompatibility.toString()
        }
    }
}