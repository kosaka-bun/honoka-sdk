package de.honoka.sdk.util.kotlin.file

import de.honoka.sdk.util.file.FileUtils
import kotlin.io.path.Path

object EnvironmentPathUtils {

    enum class BuildTool(var toolName: String) {

        MAVEN("Maven"),

        GRADLE("Gradle")
    }

    var buildTool = BuildTool.GRADLE
        set(value) {
            field = value
            dataDirPath = null
        }

    @Volatile
    private var dataDirPath: String? = null

    fun getDataDirPathOfApp(): String {
        dataDirPath?.let { return it }
        if(FileUtils.isAppRunningInJar()) {
            dataDirPath = FileUtils.getMainClasspath()
            return dataDirPath!!
        }
        dataDirPath = Path(getClassesDirPath(), "../data").run {
            normalize().toString()
        }
        return dataDirPath!!
    }

    private fun getClassesDirPath(): String {
        val relativePath = when(buildTool) {
            BuildTool.MAVEN -> ".."
            BuildTool.GRADLE -> "../.."
        }
        val classesDir = Path(FileUtils.getMainClasspath(), relativePath).run {
            normalize().toFile()
        }
        if(classesDir.name != "classes") {
            error("Not normal ${buildTool.toolName} classes directory: ${classesDir.absolutePath}")
        }
        return classesDir.absolutePath
    }
}
