import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.implementationApi

setupVersionAndPublishing(libs.versions.honoka.kotlin.utils.get())

dependencies {
    implementationApi("de.honoka.sdk:honoka-utils:1.1.2")
    implementation(libs.logback)
}

tasks {
    compileKotlin {
        dependsOn(":honoka-utils:publish")
    }
}
