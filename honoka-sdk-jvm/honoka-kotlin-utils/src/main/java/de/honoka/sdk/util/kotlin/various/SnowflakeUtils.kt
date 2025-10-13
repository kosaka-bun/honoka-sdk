package de.honoka.sdk.util.kotlin.various

import cn.hutool.core.lang.Snowflake

object SnowflakeUtils {

    private val snowflake = Snowflake(1, 1)

    fun nextId(): Long = snowflake.nextId()
}
