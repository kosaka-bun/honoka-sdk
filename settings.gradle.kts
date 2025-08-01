@file:Suppress("UnstableApiUsage")

pluginManagement {
    val customRepositories: RepositoryHandler.() -> Unit = {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
        gradlePluginPortal()
        mavenCentral()
        maven("https://mirrors.honoka.de/maven-repo/release")
        maven("https://mirrors.honoka.de/maven-repo/development")
    }
    repositories(customRepositories)
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories(customRepositories)
    }
}

rootProject.name = "honoka-sdk"

include("honoka-utils")
include("honoka-kotlin-utils")
include("honoka-spring-boot-starter")
