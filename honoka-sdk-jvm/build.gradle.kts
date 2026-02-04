import de.honoka.gradle.plugin.basic.dsl.basic
import de.honoka.gradle.plugin.basic.dsl.configs
import de.honoka.gradle.plugin.basic.dsl.honoka
import de.honoka.gradle.plugin.basic.dsl.publishing
import de.honoka.gradle.util.data.classifyProjects
import de.honoka.gradle.util.dsl.*

plugins {
    alias(commonLibs.plugins.kotlin) apply false
    alias(commonLibs.plugins.kotlin.kapt) apply false
    alias(commonLibs.plugins.kotlin.allopen) apply false
    alias(commonLibs.plugins.kotlin.lombok) apply false
    alias(commonLibs.plugins.kotlin.spring) apply false
    alias(commonLibs.plugins.honoka.basic)
}

group = "de.honoka.sdk"
version = libs.common.versions.p.root.get()

val projects = classifyProjects {
    kotlin = subprojects - projects("honoka-utils")
    springBoot = projects("honoka-spring-boot-starter")
}

subprojects {
    applier {
        java
        `java-library`
        `maven-publish`
        `honoka-basic`
    }

    group = rootProject.group

    honoka.basic {
        configs {
            java(if(project in projects.springBoot) 17 else 8, true)
        }

        dependencies {
            lombok()
        }
    }
}

projects.kotlin {
    applier {
        kotlin
        `kotlin-kapt`
        `kotlin-lombok`
        if(project !in projects.springBoot) {
            `kotlin-allopen`
        } else {
            `kotlin-spring`
        }
    }

    honoka.basic {
        configs {
            kotlin()
            kapt()
            allOpen()
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
