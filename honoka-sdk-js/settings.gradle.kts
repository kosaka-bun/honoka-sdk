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
        versionCatalogs {
            fun versionCatalogFile(name: String): ConfigurableFileCollection = run {
                files("$versionCatalogFilePrefix/$name.toml")
            }
            create("commonLibs", Action {
                from(versionCatalogFile("common"))
            })
        }
    }
}

rootProject.name = "honoka-sdk-js"
