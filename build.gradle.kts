import de.honoka.gradle.buildsrc.*
import de.honoka.gradle.buildsrc.MavenPublish.defineCheckVersionOfProjectsTask
import org.gradle.kotlin.dsl.publishing

plugins {
    @Suppress("RemoveRedundantQualifierName")
    val androidVersions = de.honoka.gradle.buildsrc.Versions.AndroidUtils
    //plugins
    java
    `java-library`
    `maven-publish`
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    id("com.android.library") version androidVersions.libraryPluginVersion apply false
    kotlin("android") version androidVersions.kotlinVersion apply false
}

group = "de.honoka.sdk"
version = "1.2.1-dev"

subprojectsCustomConfigurations {
    java {
        apply(plugin = "java")
        apply(plugin = "java-library")

        java {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
            withSourcesJar()
        }

        dependencies {
            compileOnly("org.projectlombok:lombok:1.18.26".also {
                annotationProcessor(it)
                testCompileOnly(it)
                testAnnotationProcessor(it)
            })
            //Test
            testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
            testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
        }

        tasks {
            compileJava {
                options.encoding = "UTF-8"
            }

            test {
                useJUnitPlatform()
            }
        }
    }

    android {
        apply(plugin = "com.android.library")
        apply(plugin = "org.jetbrains.kotlin.android")

        android {
            compileSdk = 33

            defaultConfig {
                minSdk = 24
                testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
            }

            buildTypes {
                release {
                    isMinifyEnabled = false
                    @Suppress("UnstableApiUsage")
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = sourceCompatibility
            }

            kotlinOptions {
                jvmTarget = compileOptions.sourceCompatibility.toString()
            }
        }

        defineSourceJarTask()

        dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.AndroidUtils.kotlinCoroutinesVersion}")
            testImplementation("junit:junit:4.13.2")
            androidTestImplementation("androidx.test.ext:junit:1.1.5")
            androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        }
    }

    common {
        apply(plugin = "maven-publish")
        apply(plugin = "io.spring.dependency-management")

        group = rootProject.group

        publishing {
            repositories {
                mavenLocal()
                if(hasProperty("remoteMavenRepositoryUrl")) {
                    maven(properties["remoteMavenRepositoryUrl"]!!)
                }
            }
        }
    }
}

defineCheckVersionOfProjectsTask()