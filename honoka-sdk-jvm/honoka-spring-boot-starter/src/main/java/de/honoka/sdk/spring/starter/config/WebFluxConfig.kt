package de.honoka.sdk.spring.starter.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@EnableConfigurationProperties(WebFluxProperties::class)
@ConditionalOnProperty(prefix = WebFluxProperties.PREFIX, name = ["enabled"])
@Configuration("${MainConfig.STARTER_BEAN_NAME_PREFIX}WebFluxConfig")
class WebFluxConfig(private val webFluxProperties: WebFluxProperties) {

    @ConditionalOnProperty(
        prefix = "${WebFluxProperties.PREFIX}.cors",
        name = ["enabled"],
        havingValue = "true"
    )
    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val config = webFluxProperties.cors.newCorsConfiguration()
        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
        return CorsWebFilter(source)
    }
}

@ConfigurationProperties(WebFluxProperties.PREFIX)
data class WebFluxProperties(

    var enabled: Boolean = true,

    @field:NestedConfigurationProperty
    var cors: WebProperties.Cors = WebProperties.Cors()
) {

    companion object {

        const val PREFIX = "honoka.webflux"
    }
}
