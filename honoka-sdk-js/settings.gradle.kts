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
            fun versionCatalogFile(name: String? = null): ConfigurableFileCollection {
                val suffix = if(name?.isBlank() == false) "-$name.toml" else ".toml"
                return files("$versionCatalogFilePrefix$suffix")
            }
            create("globalLibs", Action {
                from(versionCatalogFile())
            })
        }
    }
}

rootProject.name = "honoka-sdk-js"
