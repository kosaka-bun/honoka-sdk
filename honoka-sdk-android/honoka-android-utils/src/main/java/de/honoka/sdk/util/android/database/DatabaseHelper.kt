package de.honoka.sdk.util.android.database

import android.database.sqlite.SQLiteDatabase
import cn.hutool.json.JSONObject
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import de.honoka.sdk.util.android.various.global

internal class DatabaseHelper(
    private val dao: BaseDao<*>, databaseName: String, databaseVersion: Int
) : OrmLiteSqliteOpenHelper(
    global.application, databaseName, null, databaseVersion
) {

    @Suppress("UNCHECKED_CAST")
    private val entityClass = dao.entityClass.java as Class<Any>

    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        TableUtils.createTable(connectionSource, entityClass)
    }

    override fun onUpgrade(
        database: SQLiteDatabase, connectionSource: ConnectionSource,
        oldVersion: Int, newVersion: Int
    ) {
        val newList = ArrayList<Any>()
        val rawDao = getDao(entityClass)
        val sql = rawDao.queryBuilder().prepareStatementString()
        rawDao.queryRaw(sql).use {
            it.results.forEach { row ->
                val jo = JSONObject().apply {
                    config.isIgnoreError = true
                }
                it.columnNames.forEachIndexed { i, colName ->
                    jo[colName] = row[i]
                }
                newList.add(jo.toBean(entityClass))
            }
        }
        TableUtils.dropTable<Any, Any>(connectionSource, entityClass, false)
        TableUtils.createTable(connectionSource, entityClass)
        rawDao.create(newList)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}
