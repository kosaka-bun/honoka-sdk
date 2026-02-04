import de.honoka.gradle.plugin.android.dsl.androidTestImplementation
import de.honoka.gradle.plugin.android.ext.kotlinAndroid
import de.honoka.gradle.plugin.android.ext.useOwnProjectName
import de.honoka.gradle.plugin.basic.dsl.*
import de.honoka.gradle.util.dsl.applier
import de.honoka.gradle.util.dsl.common
import de.honoka.gradle.util.dsl.libs

plugins {
    alias(libs.plugins.android.library) apply false
    alias(commonLibs.plugins.kotlin.android) apply false
    alias(libs.plugins.honoka.android)
}

group = "de.honoka.sdk"
version = libs.common.versions.p.root.get()

subprojects {
    applier {
        `maven-publish`
        `android-library`
        `kotlin-android`
        `honoka-android`
    }

    group = rootProject.group

    honoka.basic {
        configs {
            java(8, true)
            kotlin()
        }

        dependencies {
            kotlinAndroid()
            lombok()
        }

        publishing {
            useOwnProjectName = true
        }
    }

    honoka.android {
        library {
            compileSdk = libs.versions.a.compile.sdk.get().toInt()

            defaultConfig {
                minSdk = libs.versions.a.min.sdk.get().toInt()
                testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
            }

            buildTypes {
                release {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
        }
    }

    dependencies {
        api(libs.common.honoka.kotlin.utils) {
            exclude("org.bouncycastle", "bcprov-jdk18on")
            exclude("ch.qos.logback", "logback-classic")
        }
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.2.1")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    }
}

honoka.basic {
    publishing {
        defineCheckVersionTask()
    }
}
