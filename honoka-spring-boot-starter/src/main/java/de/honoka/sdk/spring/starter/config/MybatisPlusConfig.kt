package de.honoka.sdk.spring.starter.config

import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import de.honoka.sdk.spring.starter.config.property.MybatisPlusProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan("de.honoka.sdk.spring.starter.mybatis")
@EnableConfigurationProperties(MybatisPlusProperties::class)
@ConditionalOnProperty(prefix = MybatisPlusProperties.PREFIX, name = ["enabled"])
@Configuration("${MainConfig.CONFIG_CLASS_BEAN_NAME_PREFIX}MybatisPlusConfig")
class MybatisPlusConfig {
    
    @Value("\${spring.datasource.driver-class-name}")
    private var jdbcDriverClassName: String? = null
    
    @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor = MybatisPlusInterceptor().apply {
        val dbType = jdbcDriverClassName?.lowercase()?.run {
            DbType.values().firstOrNull {
                contains(it.db.lowercase())
            }
        }
        dbType?.let { addInnerInterceptor(PaginationInnerInterceptor(it)) }
    }
}
