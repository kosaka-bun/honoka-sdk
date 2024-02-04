@file:Suppress("UnstableApiUsage")


dependencyResolutionManagement {
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
        maven("https://mirrors.honoka.de/maven-repo")
        google()
    }
}

pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

rootProject.name = "honoka-sdk"

include("honoka-utils")
include("honoka-framework-utils")
include("honoka-android-utils")
include("honoka-json")
include("honoka-json:honoka-json-api")
include("honoka-json:honoka-json-gson")
include("honoka-json:honoka-json-fastjson")
