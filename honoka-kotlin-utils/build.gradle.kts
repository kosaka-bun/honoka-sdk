import de.honoka.gradle.plugin.basic.ext.MavenPublishDsl.default
import de.honoka.gradle.util.dsl.implementationApi

dependencies {
    implementationApi("de.honoka.sdk:honoka-utils:1.1.3-dev")
    implementation(libs.logback)
}

tasks {
    compileKotlin {
        dependsOn(":honoka-utils:publish")
    }
}

publishing {
    publications {
        default(libs.versions.honoka.kotlin.utils.get())
    }
}
