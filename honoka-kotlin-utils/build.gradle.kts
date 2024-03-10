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
    arrayOf(
        "de.honoka.sdk:honoka-utils:1.0.10",
        "org.dom4j:dom4j:2.1.3",
        "cn.hutool:hutool-all:5.8.18"
    ).forEach {
        implementation(it) {
            exclude("gui.ava", "html2image")
        }
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