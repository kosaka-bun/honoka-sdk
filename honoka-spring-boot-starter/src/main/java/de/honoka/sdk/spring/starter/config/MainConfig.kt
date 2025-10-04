package de.honoka.sdk.spring.starter.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@ComponentScan("de.honoka.sdk.spring.starter.core")
@EnableConfigurationProperties(MainProperties::class)
@Configuration("${MainConfig.STARTER_BEAN_NAME_PREFIX}MainConfig")
class MainConfig(private val mainProperties: MainProperties) {
    
    companion object {
        
        const val STARTER_BEAN_NAME_PREFIX = "honokaStarter"
    }

    @ConditionalOnProperty(
        prefix = "${MainProperties.PREFIX}.cors",
        name = ["enabled"],
        havingValue = "true"
    )
    @Bean
    fun corsFilter(): CorsFilter {
        val config = CorsConfiguration().apply {
            allowCredentials = true
            mainProperties.cors.run {
                addAllowedHeader(header)
                addAllowedMethod(method)
                origins.run {
                    if(isEmpty()) {
                        addAllowedOriginPattern("*")
                    } else {
                        forEach {
                            //必须以域名结尾，包含带端口和不带端口两种情况
                            addAllowedOriginPattern("*://*$it")
                            addAllowedOriginPattern("*://*$it:*")
                        }
                    }
                }
            }
        }
        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
        return CorsFilter(source)
    }
}

@ConfigurationProperties(MainProperties.PREFIX)
class MainProperties(

    var cors: Cors = Cors()
) {
    
    companion object {
        
        const val PREFIX = "honoka.starter"
    }

    data class Cors(

        var enabled: Boolean = false,

        var origins: List<String> = listOf(),

        var header: String = "*",

        var method: String = "*"
    )
}
