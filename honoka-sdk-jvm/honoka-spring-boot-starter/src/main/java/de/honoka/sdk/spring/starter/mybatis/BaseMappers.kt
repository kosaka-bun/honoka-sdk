package de.honoka.sdk.spring.starter.mybatis

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import de.honoka.sdk.util.kotlin.bean.copyFrom
import kotlin.reflect.full.createInstance

inline fun <reified T : Any> BaseMapper<T>.queryChainWrapper(): KtQueryChainWrapper<T> =
    ChainWrappers.ktQueryChain(this, T::class.java)

inline fun <reified T : Any> BaseMapper<T>.updateChainWrapper(): KtUpdateChainWrapper<T> =
    ChainWrappers.ktUpdateChain(this, T::class.java)

inline fun <reified T : Any> BaseMapper<T>.query(
    limit: Long? = null, block: KtQueryChainWrapper<T>.() -> Unit
): List<T> = queryChainWrapper().run {
    block()
    if(limit == null) {
        list()
    } else {
        list(Page(1, limit, false))
    }
}

inline fun <reified T : Any> BaseMapper<T>.queryBy(
    params: Any, limit: Long? = null
): List<T> {
    val queryWrapper = QueryWrapper(T::class.createInstance().copyFrom(params))
    return if(limit == null) {
        selectList(queryWrapper)
    } else {
        selectPage(Page(1, limit, false), queryWrapper).records
    }
}

inline fun <reified T : Any> BaseMapper<T>.firstOrNull(block: KtQueryChainWrapper<T>.() -> Unit): T? =
    query(1, block).firstOrNull()

inline fun <reified T : Any> BaseMapper<T>.first(block: KtQueryChainWrapper<T>.() -> Unit): T = firstOrNull(block)!!

inline fun <reified T : Any> BaseMapper<T>.firstOrNullBy(params: Any): T? = queryBy(params, 1).firstOrNull()

inline fun <reified T : Any> BaseMapper<T>.firstBy(params: Any): T = firstOrNullBy(params)!!

inline fun <reified T : Any> BaseMapper<T>.exists(block: KtQueryChainWrapper<T>.() -> Unit): Boolean =
    firstOrNull(block) != null

inline fun <reified T : Any> BaseMapper<T>.existsBy(params: Any): Boolean = firstOrNullBy(params) != null

inline fun <reified T : Any> BaseMapper<T>.existsById(id: Any): Boolean {
    val one = firstOrNull {
        eq(MyBatisPlusUtils.getTableIdProp(T::class), id)
    }
    return one != null
}

inline fun <reified T : Any> BaseMapper<T>.update(block: KtUpdateChainWrapper<T>.() -> Unit): Boolean =
    updateChainWrapper().apply(block).update()

inline fun <reified T : Any> BaseMapper<T>.delete(block: KtUpdateChainWrapper<T>.() -> Unit): Boolean =
    updateChainWrapper().apply(block).remove()
