plugins {
    alias(libs.plugins.android.library) apply false
    alias(globalLibs.plugins.kotlin.android) apply false
    alias(libs.plugins.honoka.android)
}

version = globalLibs.versions.p.root.get()

allprojects {
    group = "de.honoka.sdk"
}

subprojects {
    apply(plugin = "de.honoka.gradle.plugin.android")
}

honoka.basic {
    publishing {
        defineCheckVersionTask()
    }
}
