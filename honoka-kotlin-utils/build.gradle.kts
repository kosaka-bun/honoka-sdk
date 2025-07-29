import de.honoka.gradle.util.dsl.implementationApi

version = libs.versions.p.honoka.kotlin.utils.get()

dependencies {
    implementationApi(libs.hku.honoka.utils)
    implementation(libs.logback)
}

honoka {
    basic {
        publishing {
            publications {
                default()
            }
        }
    }
}
