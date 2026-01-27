import de.honoka.gradle.util.dsl.*

honoka.basic.publishing.version = libs.common.versions.p.honoka.kotlin.utils.get()

dependencies {
    api(libs.common.honoka.utils)
    implementation(libs.logback)
}
