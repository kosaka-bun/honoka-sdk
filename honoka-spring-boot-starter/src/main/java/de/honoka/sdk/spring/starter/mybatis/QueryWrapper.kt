package de.honoka.sdk.spring.starter.mybatis

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers

inline fun <reified T : Any> BaseMapper<T>.queryChainWrapper(): KtQueryChainWrapper<T> = run {
    ChainWrappers.ktQueryChain(this, T::class.java)
}
