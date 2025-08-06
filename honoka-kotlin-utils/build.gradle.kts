version = libs.versions.p.honoka.kotlin.utils.get()

dependencies {
    api(libs.hku.honoka.utils)
    implementation(libs.logback)
}

honoka {
    basic {
        publishing {
            default()
        }
    }
}
