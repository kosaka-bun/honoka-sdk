version = commonLibs.versions.p.honoka.kotlin.utils.get()

dependencies {
    api(commonLibs.honoka.utils)
    implementation(libs.logback)
}

honoka.basic {
    publishing {
        default()
    }
}
