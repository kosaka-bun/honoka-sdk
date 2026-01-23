package de.honoka.sdk.spring.starter.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator

@ComponentScan(
    "de.honoka.sdk.spring.starter.core",
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class
)
@Configuration
class MainConfig
