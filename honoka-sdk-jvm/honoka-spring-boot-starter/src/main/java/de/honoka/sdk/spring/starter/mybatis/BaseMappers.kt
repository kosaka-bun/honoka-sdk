package de.honoka.sdk.spring.starter.mybatis

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers

inline fun <reified T : Any> BaseMapper<T>.queryChainWrapper(): KtQueryChainWrapper<T> =
    ChainWrappers.ktQueryChain(this, T::class.java)

inline fun <reified T : Any> BaseMapper<T>.updateChainWrapper(): KtUpdateChainWrapper<T> =
    ChainWrappers.ktUpdateChain(this, T::class.java)

inline fun <reified T : Any> BaseMapper<T>.query(
    limit: Long = 0, block: KtQueryChainWrapper<T>.() -> Unit
): List<T> = queryChainWrapper().run {
    apply(block)
    if(limit < 1) {
        list()
    } else {
        list(Page(1, limit, false))
    }
}

inline fun <reified T : Any> BaseMapper<T>.firstOrNull(block: KtQueryChainWrapper<T>.() -> Unit): T? =
    query(1, block).firstOrNull()

inline fun <reified T : Any> BaseMapper<T>.first(block: KtQueryChainWrapper<T>.() -> Unit): T = firstOrNull(block)!!

inline fun <reified T : Any> BaseMapper<T>.update(block: KtUpdateChainWrapper<T>.() -> Unit): Boolean =
    updateChainWrapper().apply(block).update()
