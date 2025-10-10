plugins {
    alias(commonLibs.plugins.honoka.basic)
}

version = commonLibs.versions.p.root.get()

layout.buildDirectory = File("./gradle-build")

honoka.basic {
    publishing {
        defineCheckVersionTask()
    }
}
