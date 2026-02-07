package de.honoka.sdk.spring.starter.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.context.annotation.*
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.filter.CorsFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource as ReactiveUrlCorsConfigSource

@ComponentScan(
    "de.honoka.sdk.spring.starter.web",
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASPECTJ,
            pattern = ["de.honoka.sdk.spring.starter.web.webflux..*"]
        )
    ],
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class
)
@ConditionalOnProperty(
    prefix = WebFluxProperties.PREFIX, name = ["enabled"], havingValue = "false", matchIfMissing = true
)
@ConditionalOnProperty(prefix = WebProperties.PREFIX, name = ["enabled"], matchIfMissing = true)
@Configuration
class WebConfig(private val webProperties: WebProperties) {

    @ConditionalOnProperty(prefix = "${WebProperties.PREFIX}.cors", name = ["enabled"])
    @Bean
    fun corsFilter(): CorsFilter {
        val config = webProperties.cors.newCorsConfiguration()
        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
        return CorsFilter(source)
    }
}

@ComponentScan(
    "de.honoka.sdk.spring.starter.web.webflux",
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class
)
@ConditionalOnProperty(prefix = WebFluxProperties.PREFIX, name = ["enabled"])
@Configuration
class WebFluxConfig(private val webFluxProperties: WebFluxProperties) {

    @ConditionalOnProperty(prefix = "${WebFluxProperties.PREFIX}.cors", name = ["enabled"])
    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val config = webFluxProperties.cors.newCorsConfiguration()
        val source = ReactiveUrlCorsConfigSource().apply {
            registerCorsConfiguration("/**", config)
        }
        return CorsWebFilter(source)
    }
}

@ConfigurationProperties(WebProperties.PREFIX)
data class WebProperties(

    var enabled: Boolean = true,

    var returnStackTraceOnError: Boolean = false,

    var cors: Cors = Cors()
) {

    companion object {

        const val PREFIX = "honoka.web"
    }

    data class Cors(

        var enabled: Boolean = false,

        var origins: List<String> = listOf(),

        var header: String = "*",

        var method: String = "*"
    ) {

        internal fun newCorsConfiguration(): CorsConfiguration = CorsConfiguration().apply {
            allowCredentials = true
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
}

@ConfigurationProperties(WebFluxProperties.PREFIX)
data class WebFluxProperties(

    var enabled: Boolean = false,

    var returnStackTraceOnError: Boolean = false,

    @NestedConfigurationProperty
    var cors: WebProperties.Cors = WebProperties.Cors(),

    var gateway: Gateway = Gateway()
) {

    companion object {

        const val PREFIX = "honoka.webflux"
    }

    data class Gateway(

        var enabled: Boolean = false
    )
}
