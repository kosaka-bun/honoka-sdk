version = globalLibs.versions.p.honoka.kotlin.utils.get()

dependencies {
    api(globalLibs.honoka.utils)
    implementation(libs.logback)
}

honoka.basic {
    publishing {
        default()
    }
}
