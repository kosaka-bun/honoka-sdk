package de.honoka.sdk.spring.starter.core.database;

import de.honoka.sdk.util.file.AbstractEnvironmentPathUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Paths;

public abstract class AbstractEmbeddedDatabaseUtils {

    public enum DbType {

        SQLITE, H2
    }

    private final AbstractEnvironmentPathUtils environmentPathUtils;

    public AbstractEmbeddedDatabaseUtils(AbstractEnvironmentPathUtils environmentPathUtils) {
        this.environmentPathUtils = environmentPathUtils;
    }

    /**
     * 获取相对于Java应用数据目录（由{@link AbstractEnvironmentPathUtils#getDataDirPathOfApp
     * AbstractEnvironmentPathUtils.getDataDirPathOfApp}方法获得的一个自定义目录）的嵌入式数据库的JDBC URL
     */
    public String getJdbcUrlRelatedWithDataDir(DbType dbType, String databaseFilePath) {
        String dataDirPath = environmentPathUtils.getDataDirPathOfApp().replace("\\", "/");
        if(!dataDirPath.endsWith("/")) dataDirPath += "/";
        databaseFilePath = StringUtils.stripStart(databaseFilePath, "/\\");
        String absoluteDatabaseFilePath = dataDirPath + databaseFilePath;
        String absoluteDatabaseFileDirPath = absoluteDatabaseFilePath.substring(0, absoluteDatabaseFilePath.lastIndexOf("/"));
        File absoluteDatabaseFileDir = Paths.get(absoluteDatabaseFileDirPath).toFile();
        if(!absoluteDatabaseFileDir.exists()) absoluteDatabaseFileDir.mkdirs();
        @SuppressWarnings("UnnecessaryLocalVariable")
        String jdbcUrl = switch(dbType) {
            case SQLITE -> "jdbc:sqlite:" + absoluteDatabaseFilePath + ".db";
            case H2 -> "jdbc:h2:" + absoluteDatabaseFilePath + ";auto_server=true";
        };
        return jdbcUrl;
    }

    public void setJdbcUrlRelatedWithDataDirInJvmProps(DbType dbType, String databaseFilePath) {
        String propKey = "spring.datasource.url";
        if(StringUtils.isNotBlank(System.getProperty(propKey))) return;
        String jdbcUrl = getJdbcUrlRelatedWithDataDir(dbType, databaseFilePath);
        System.setProperty(propKey, jdbcUrl);
    }
}
