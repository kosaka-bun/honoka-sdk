package de.honoka.sdk.util.android.database

import android.database.sqlite.SQLiteDatabase
import cn.hutool.core.bean.BeanUtil
import cn.hutool.json.JSONObject
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import de.honoka.sdk.util.android.common.GlobalComponents

internal class DatabaseHelper(
    private val dao: BaseDao<*>, databaseName: String, databaseVersion: Int
) : OrmLiteSqliteOpenHelper(GlobalComponents.application, databaseName, null, databaseVersion) {

    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        TableUtils.createTable(connectionSource, dao.entityClass)
    }

    override fun onUpgrade(database: SQLiteDatabase, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int) {
        val newList = ArrayList<Any>()
        val currentRowData = ArrayList<JSONObject>()
        val rawDao: Dao<*, *> = getDao(dao.entityClass)
        val sql = rawDao.queryBuilder().prepareStatementString()
        rawDao.queryRaw(sql).use {
            it.results.forEach { row ->
                val jo = JSONObject()
                it.columnNames.forEachIndexed { i, colName ->
                    jo[colName] = row[i]
                }
                currentRowData.add(jo)
            }
        }
        /*
         * Kotlin调用泛型方法时，若存在无法推断具体类型的泛型，则必须在调用时明确指明泛型的类型，且泛型不得为通配符（*）。
         *
         * OrmLite库中原有的TableUtils.dropTable()方法存在上述情况，Kotlin要求必须写明EntityClass的泛型类型，但此处
         * 的泛型类型必须是通配符，因此必须调用经过Java代码包装的方法。
         */
        OrmLiteUtils.dropTable(connectionSource, dao.entityClass, false)
        TableUtils.createTable(connectionSource, dao.entityClass)
        currentRowData.forEach {
            newList.add(BeanUtil.toBeanIgnoreError(it, dao.entityClass)!!)
        }
        OrmLiteUtils.insertCollection(rawDao, newList)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = onUpgrade(db, oldVersion, newVersion)
}