plugins {
    alias(globalLibs.plugins.honoka.basic)
}

version = globalLibs.versions.p.root.get()

layout.buildDirectory = File("./gradle-build")

honoka.basic {
    publishing {
        defineCheckVersionTask()
    }
}
