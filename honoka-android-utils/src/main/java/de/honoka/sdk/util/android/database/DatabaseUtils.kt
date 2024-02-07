package de.honoka.sdk.util.android.database

abstract class AbstractAndroidDatabaseUtils {

    protected abstract val daoInstances: List<BaseDao<*>>

    /**
     * 仅用于触发所有Dao实例的构造方法，不执行任何其他操作（主要是初始化在Kotlin中被声明为object类型的Dao对象）
     */
    fun initDaoInstances() {
        daoInstances
    }
}