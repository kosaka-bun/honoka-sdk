import de.honoka.gradle.plugin.basic.dsl.*
import de.honoka.gradle.util.data.classifyProjects
import de.honoka.gradle.util.dsl.*

plugins {
    alias(commonLibs.plugins.kotlin) apply false
    alias(commonLibs.plugins.kotlin.kapt) apply false
    alias(commonLibs.plugins.kotlin.lombok) apply false
    alias(commonLibs.plugins.honoka.basic)
}

group = "de.honoka.sdk"
version = libs.common.versions.p.root.get()

val projects = classifyProjects {
    kotlin = subprojects - projects("honoka-utils")
}

subprojects {
    applier {
        java
        `java-library`
        `maven-publish`
        alias(libs.common.plugins.honoka.basic)
    }

    group = rootProject.group

    honoka.basic {
        configs {
            java(8, true)
            javaTask()
        }

        dependencies {
            lombok()
        }
    }
}

projects.kotlin {
    applier {
        alias(libs.common.plugins.kotlin)
        alias(libs.common.plugins.kotlin.kapt)
        alias(libs.common.plugins.kotlin.lombok)
    }

    honoka.basic {
        configs {
            kotlin()
            kapt()
        }

        dependencies {
            kotlin()
        }
    }
}

honoka.basic {
    publishing {
        defineCheckVersionTask()
    }
}
