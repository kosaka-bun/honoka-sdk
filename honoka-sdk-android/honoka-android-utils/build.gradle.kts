import de.honoka.gradle.plugin.android.ext.aarVersion
import de.honoka.gradle.util.dsl.*

honoka.basic.publishing.aarVersion = libs.common.versions.p.honoka.android.utils.get()

android {
    namespace = "de.honoka.sdk.util.android"

    sourceSets["main"].java {
        srcDir("/src/patch/java")
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    api("com.j256.ormlite:ormlite-android:5.1")
    api(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.cors)
}
