import de.honoka.gradle.plugin.basic.ext.MavenPublishDsl.default
import de.honoka.gradle.util.dsl.implementationApi

version = libs.versions.p.honoka.kotlin.utils.get()

dependencies {
    implementationApi(libs.hku.honoka.utils)
    implementation(libs.logback)
}

tasks {
    compileKotlin {
        dependsOn(":honoka-utils:publish")
    }
}

publishing {
    publications {
        default()
    }
}
