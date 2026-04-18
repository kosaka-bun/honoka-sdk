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
            dataDirPathOrNull = null
        }

    var pathOffset = ""

    @Volatile
    private var dataDirPathOrNull: String? = null

    val dataDirPath: String
        get() {
            dataDirPathOrNull?.let { return it }
            dataDirPathOrNull = if(FileUtils.isAppRunningInJar()) {
                val mainClasspath = FileUtils.getMainClasspath()
                if(pathOffset.isBlank()) {
                    mainClasspath
                } else {
                    Path(mainClasspath, pathOffset).normalize().toString()
                }
            } else {
                Path(getClassesDirPath(), "../data$pathOffset").normalize().toString()
            }
            return dataDirPathOrNull!!
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
