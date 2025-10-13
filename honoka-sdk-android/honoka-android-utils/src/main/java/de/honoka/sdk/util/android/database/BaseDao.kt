package de.honoka.sdk.util.android.database

import cn.hutool.core.util.ObjectUtil
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.stmt.QueryBuilder
import com.j256.ormlite.table.DatabaseTable
import de.honoka.sdk.util.kotlin.text.singleLine
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseDao<T : Any>(internal val entityClass: KClass<T>) {

    private val databaseHelper: DatabaseHelper

    val rawDao: Dao<T, Any>

    private val entityCache = ConcurrentHashMap<Any, T>()

    private val idProp: KMutableProperty1<T, *> = entityClass.run {
        val prop = declaredMemberProperties.first {
            val clazz = DatabaseField::class.java
            it.javaField!!.getAnnotation(clazz)?.id ?: false
        }
        prop as KMutableProperty1<T, *>
    }

    init {
        databaseHelper = initDatabaseHelper()
        rawDao = databaseHelper.getDao(entityClass.java)
    }

    private fun initDatabaseHelper(): DatabaseHelper {
        val ormLiteTableAnnotation = entityClass.findAnnotation<DatabaseTable>()
        val tableAnnotation = entityClass.findAnnotation<Table>()
        if(ObjectUtil.hasNull(ormLiteTableAnnotation, tableAnnotation)) {
            val msg = """
                Class ${entityClass.qualifiedName} has no @DatabaseTable or @Table. |
                Both of them are required.
            """.singleLine()
            error(msg)
        }
        fun newHelper(): DatabaseHelper = DatabaseHelper(
            this, "${ormLiteTableAnnotation!!.tableName}.db",
            tableAnnotation!!.version
        )
        //先创建一个DatabaseHelper，然后手动触发数据库的初始化（创建数据库文件以及数据表）、升级或降级
        newHelper().run {
            writableDatabase
            close()
        }
        /*
         * 如果使用创建的第一个DatabaseHelper触发升级的过程中，程序确实进行了升级的行为，那么第一个
         * DatabaseHelper就不再应当留作后续使用，应该关闭这个DatabaseHelper，创建一个新的
         * DatabaseHelper，用于后续使用。
         *
         * 由于数据库升级可能会修改表的结构，而原生的SqliteDatabaseOpenHelper可能对原有的表结构存在
         * 缓存，这会导致进行过数据库升级的DatabaseHelper所创建的Dao对象在第一次对升级后的数据库进行
         * 查询时，只能查询到原有表结构字段所包含的数据。由于原有表结构字段并不一定能正确映射到新的实
         * 体类当中，所以上述Dao对象在第一次对升级后的数据库进行查询时将会出现异常。
         */
        return newHelper()
    }

    private fun buildQuery(
        condition: QueryBuilder<T, Any>.() -> Unit
    ): PreparedQuery<T> = rawDao.queryBuilder().apply(condition).prepare()

    fun listAll(): List<T> = rawDao.queryForAll()

    private fun getById(id: Any, useCache: Boolean): T? {
        if(useCache) {
            entityCache[id]?.let {
                return it
            }
        } else {
            entityCache.remove(id)
        }
        return rawDao.queryForId(id)?.also {
            entityCache[id] = it
        }
    }

    fun getById(id: Any): T? = getById(id, false)

    fun getByIdCached(id: Any): T? = getById(id, true)

    fun query(condition: QueryBuilder<T, Any>.() -> Unit): List<T> = run {
        rawDao.query(buildQuery(condition))
    }

    fun queryOne(condition: QueryBuilder<T, Any>.() -> Unit): T? = run {
        query {
            condition()
            limit(1)
        }.firstOrNull()
    }

    private fun getId(entity: T): Any = idProp.get(entity)!!

    @Synchronized
    fun save(entity: T): Int = rawDao.create(entity)

    @Synchronized
    fun updateById(entity: T) {
        rawDao.update(entity)
        entityCache.remove(getId(entity))
    }

    @Synchronized
    fun saveOrUpdate(entity: T) {
        rawDao.createOrUpdate(entity)
        entityCache.remove(getId(entity))
    }

    @Synchronized
    fun deleteById(id: Any) {
        rawDao.deleteById(id)
        entityCache.remove(id)
    }
}
