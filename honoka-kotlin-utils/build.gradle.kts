import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin)
}

setupVersionAndPublishing(libs.versions.honoka.kotlin.utils.get())

dependencies {
    kotlin(project)
    arrayOf(
        "de.honoka.sdk:honoka-utils:1.0.11",
        "cn.hutool:hutool-all:5.8.18"
    ).forEach {
        implementation(it)
        api(it)
    }
}

tasks {
    compileJava {
        dependsOn(":honoka-utils:publish")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = java.sourceCompatibility.toString()
        }
    }
}