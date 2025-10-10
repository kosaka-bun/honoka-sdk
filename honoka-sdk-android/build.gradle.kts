plugins {
    alias(libs.plugins.android.library) apply false
    alias(commonLibs.plugins.kotlin.android) apply false
    alias(libs.plugins.honoka.android)
}

version = commonLibs.versions.p.root.get()

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
