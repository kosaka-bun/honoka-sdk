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
        "de.honoka.sdk:honoka-utils:1.0.12-dev",
        "cn.hutool:hutool-all:5.8.18"
    ).forEach {
        implementation(it)
        api(it)
    }
    //仅用于避免libs.versions.toml中产生version变量未使用的提示
    libs.versions.kotlin.coroutines
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