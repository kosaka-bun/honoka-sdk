package de.honoka.sdk.spring.starter.web.webflux

import de.honoka.sdk.spring.starter.config.WebFluxProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@ConditionalOnProperty(prefix = "${WebFluxProperties.PREFIX}.gateway", name = ["enabled"])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
internal annotation class ForGateway

@ConditionalOnProperty(
    prefix = "${WebFluxProperties.PREFIX}.gateway", name = ["enabled"],
    havingValue = "false", matchIfMissing = true
)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
internal annotation class NotForGateway
