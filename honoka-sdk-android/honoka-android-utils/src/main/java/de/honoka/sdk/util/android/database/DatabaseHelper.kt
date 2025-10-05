package de.honoka.sdk.util.android.database

import android.database.sqlite.SQLiteDatabase
import cn.hutool.json.JSONObject
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import de.honoka.sdk.util.android.basic.global

internal class DatabaseHelper(
    private val dao: BaseDao<*>, databaseName: String, databaseVersion: Int
) : OrmLiteSqliteOpenHelper(
    global.application, databaseName, null, databaseVersion
) {

    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        TableUtils.createTable(connectionSource, dao.entityClass.java)
    }

    override fun onUpgrade(
        database: SQLiteDatabase, connectionSource: ConnectionSource,
        oldVersion: Int, newVersion: Int
    ) {
        val newList = ArrayList<Any>()
        val rawDao: Dao<*, *> = getDao(dao.entityClass.java)
        val sql = rawDao.queryBuilder().prepareStatementString()
        rawDao.queryRaw(sql).use {
            it.results.forEach { row ->
                val jo = JSONObject().apply {
                    config.isIgnoreError = true
                }
                it.columnNames.forEachIndexed { i, colName ->
                    jo[colName] = row[i]
                }
                newList.add(jo.toBean(dao.entityClass.java))
            }
        }
        /*
         * Kotlin调用泛型方法时，若存在无法推断具体类型的泛型，则必须在调用时明确指明泛型的类型，
         * 且泛型不得为通配符（*）。
         *
         * OrmLite库中原有的TableUtils.dropTable()方法存在上述情况，Kotlin要求必须写明
         * EntityClass的泛型类型，但此处的泛型类型必须是通配符，因此必须调用经过Java代码包装的方法。
         */
        OrmLiteUtils.dropTable(connectionSource, dao.entityClass.java, false)
        TableUtils.createTable(connectionSource, dao.entityClass.java)
        OrmLiteUtils.insertCollection(rawDao, newList)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}
