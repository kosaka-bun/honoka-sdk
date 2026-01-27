@file:Suppress("UnstableApiUsage")

pluginManagement {
    val customRepositories: RepositoryHandler.() -> Unit = {
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        gradlePluginPortal()
        mavenLocal()
        maven("https://mirrors.honoka.de/maven-repo/release")
        maven("https://mirrors.honoka.de/maven-repo/development")
    }
    val versionCatalogFilePrefix = "../gradle/versions"
    repositories(customRepositories)
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories(customRepositories)
        versionCatalogs {
            fun versionCatalogFile(name: String): ConfigurableFileCollection =
                files("$versionCatalogFilePrefix/$name.toml")
            create("commonLibs") {
                from(versionCatalogFile("common"))
            }
            create("libs") {
                from(versionCatalogFile("jvm"))
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "honoka-sdk-jvm"

include("honoka-utils")
include("honoka-kotlin-utils")
include("honoka-spring-boot-starter")
