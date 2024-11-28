import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.implementationApi

setupVersionAndPublishing(libs.versions.honoka.kotlin.utils.get())

dependencies {
    implementationApi("de.honoka.sdk:honoka-utils:2.0.0-dev")
    implementationApi("cn.hutool:hutool-all:5.8.18")
}

tasks {
    compileKotlin {
        dependsOn(":honoka-utils:publish")
    }
}