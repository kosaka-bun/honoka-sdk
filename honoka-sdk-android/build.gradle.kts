plugins {
    alias(libs.plugins.android.library) apply false
    alias(commonLibs.plugins.kotlin.android) apply false
    alias(libs.plugins.honoka.android)
}

group = "de.honoka.sdk"
version = commonLibs.versions.p.root.get()

subprojects {
    group = rootProject.group

    apply(plugin = "de.honoka.gradle.plugin.android")
}

honoka.basic {
    publishing {
        defineCheckVersionTask()
    }
}
