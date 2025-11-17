package de.honoka.sdk.spring.starter.core.database

import de.honoka.sdk.util.kotlin.file.EnvironmentPathUtils
import kotlin.io.path.Path

object EmbeddedDatabaseUtils {

    enum class DbType {

        SQLITE, H2
    }

    /**
     * 获取相对于Java应用数据目录（由[EnvironmentPathUtils.getDataDirPathOfApp]
     * 方法获得的一个自定义目录）的嵌入式数据库的JDBC URL。
     */
    fun getJdbcUrlRelatedWithDataDir(dbType: DbType, databaseFilePath: String): String {
        val dataDirPath = EnvironmentPathUtils.getDataDirPathOfApp().run {
            val p = replace("\\", "/")
            if(p.endsWith("/")) p else "$p/"
        }
        val databaseFilePath = databaseFilePath.removePrefix("/\\")
        val absoluteDatabaseFilePath = "$dataDirPath$databaseFilePath"
        val absoluteDatabaseFileDirPath = absoluteDatabaseFilePath.run {
            substring(0, lastIndexOf("/"))
        }
        Path(absoluteDatabaseFileDirPath).toFile().run {
            if(!exists()) mkdirs()
        }
        val jdbcUrl = when(dbType) {
            DbType.SQLITE -> "jdbc:sqlite:${absoluteDatabaseFilePath}.db"
            DbType.H2 -> "jdbc:h2:$absoluteDatabaseFilePath;auto_server=true"
        }
        return jdbcUrl
    }

    fun setJdbcUrlRelatedWithDataDirInJvmProps(dbType: DbType, databaseFilePath: String) {
        val propKey = "spring.datasource.url"
        if(System.getProperty(propKey)?.isNotBlank() == true) return
        val jdbcUrl = getJdbcUrlRelatedWithDataDir(dbType, databaseFilePath)
        System.setProperty(propKey, jdbcUrl)
    }
}
