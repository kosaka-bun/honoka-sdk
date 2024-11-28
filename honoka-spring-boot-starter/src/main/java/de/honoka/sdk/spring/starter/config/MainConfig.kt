package de.honoka.sdk.spring.starter.config

import de.honoka.sdk.spring.starter.config.property.MainProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan("de.honoka.sdk.spring.starter.core")
@EnableConfigurationProperties(MainProperties::class)
@Configuration
class MainConfig 
