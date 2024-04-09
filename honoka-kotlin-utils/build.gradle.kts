import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val versions = de.honoka.gradle.buildsrc.Versions
    //plugins
    kotlin("jvm") version versions.kotlinVersion
}

setupVersionAndPublishing("1.0.1-dev")

dependencies {
    kotlin()
    arrayOf(
        "de.honoka.sdk:honoka-utils:1.0.11",
        "cn.hutool:hutool-all:5.8.18"
    ).forEach {
        implementation(it)
        api(it)
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = java.sourceCompatibility.toString()
        }
    }
}