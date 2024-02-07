package de.honoka.sdk.util.android.common

import cn.hutool.core.lang.Snowflake

object SnowflakeUtils {

    private val snowflake = Snowflake(15)

    fun nextId(): Long = snowflake.nextId()
}