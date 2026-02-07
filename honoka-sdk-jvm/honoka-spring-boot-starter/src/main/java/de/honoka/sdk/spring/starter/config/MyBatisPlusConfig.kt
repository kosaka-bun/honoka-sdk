package de.honoka.sdk.spring.starter.config

import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import de.honoka.sdk.spring.starter.core.SpringPropertiesHolder
import de.honoka.sdk.util.kotlin.various.log
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.RollbackOn

@EnableTransactionManagement(rollbackOn = RollbackOn.ALL_EXCEPTIONS)
@ComponentScan(
    "de.honoka.sdk.spring.starter.mybatis",
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class
)
@ConditionalOnProperty(prefix = MyBatisPlusProperties.PREFIX, name = ["enabled"])
@Configuration
class MyBatisPlusConfig(
    private val springPropertiesHolder: SpringPropertiesHolder,
    private val mybatisPlusProperties: MyBatisPlusProperties
) {

    @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor = MybatisPlusInterceptor().apply {
        val dbType = mybatisPlusProperties.dbType ?: run {
            springPropertiesHolder.jdbcDriverClassName?.lowercase()?.run {
                DbType.entries.firstOrNull { contains(it.db.lowercase()) }
            }
        } ?: DbType.OTHER
        log.info("Use DbType: ${dbType.name}")
        addInnerInterceptor(PaginationInnerInterceptor(dbType))
    }
}

@ConditionalOnClass(DbType::class)
@ConfigurationProperties(MyBatisPlusProperties.PREFIX)
data class MyBatisPlusProperties(
    
    var enabled: Boolean = false,

    var dbType: DbType? = null
) {

    companion object {

        const val PREFIX = "honoka.mybatis"
    }
}
