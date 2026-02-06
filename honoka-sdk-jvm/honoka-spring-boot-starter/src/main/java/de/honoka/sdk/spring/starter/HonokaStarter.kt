package de.honoka.sdk.spring.starter

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator

@ConfigurationPropertiesScan
@ComponentScan(
    "de.honoka.sdk.spring.starter.config",
    "de.honoka.sdk.spring.starter.core",
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class
)
@AutoConfiguration
class HonokaStarter
