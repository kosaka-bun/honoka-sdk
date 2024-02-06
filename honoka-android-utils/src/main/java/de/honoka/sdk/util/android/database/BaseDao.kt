package de.honoka.sdk.util.android.database

import cn.hutool.core.util.ObjectUtil
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.table.DatabaseTable

abstract class BaseDao<T>(internal val entityClass: Class<T>) {

    private val databaseHelper: DatabaseHelper

    @Suppress("MemberVisibilityCanBePrivate")
    val rawDao: Dao<T, Any>

    init {
        databaseHelper = initDatabaseHelper()
        rawDao = databaseHelper.getDao(entityClass)
    }

    private fun initDatabaseHelper(): DatabaseHelper {
        val ormLiteTableAnnotation = entityClass.getAnnotation(DatabaseTable::class.java)
        val tableAnnotation = entityClass.getAnnotation(Table::class.java)
        if(ObjectUtil.hasNull(ormLiteTableAnnotation, tableAnnotation)) {
            throw Exception("Class ${entityClass.name} has no @DatabaseTable or @Table. Both of them are required.")
        }
        fun newHelper() = DatabaseHelper(
            this, "${ormLiteTableAnnotation!!.tableName}.db", tableAnnotation!!.version
        )
        //先创建一个DatabaseHelper，然后手动触发数据库的初始化（创建数据库文件以及数据表）、升级或降级
        newHelper().run {
            writableDatabase
            close()
        }
        /*
         * 如果使用创建的第一个DatabaseHelper触发升级的过程中，程序确实进行了升级的行为，那么第一个DatabaseHelper就不再
         * 应当留作后续使用，应该关闭这个DatabaseHelper，创建一个新的DatabaseHelper，用于后续使用。
         *
         * 由于数据库升级可能会修改表的结构，而原生的SqliteDatabaseOpenHelper可能对原有的表结构存在缓存，这会导致进行过
         * 数据库升级的DatabaseHelper所创建的Dao对象在第一次对升级后的数据库进行查询时，只能查询到原有表结构字段所包含的
         * 数据。由于原有表结构字段并不一定能正确映射到新的实体类当中，所以上述Dao对象在第一次对升级后的数据库进行查询时将
         * 会出现异常。
         */
        return newHelper()
    }
}