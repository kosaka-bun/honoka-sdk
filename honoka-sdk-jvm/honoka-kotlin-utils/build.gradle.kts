import de.honoka.gradle.util.dsl.common
import de.honoka.gradle.util.dsl.libs

honoka.basic.publishing.version = libs.common.versions.p.honoka.kotlin.utils.get()

dependencies {
    implementation(libs.logback)
    api(libs.common.honoka.utils)
}
