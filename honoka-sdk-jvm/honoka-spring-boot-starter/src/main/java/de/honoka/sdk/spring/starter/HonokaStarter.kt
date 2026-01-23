package de.honoka.sdk.spring.starter

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator

@ComponentScan(
    "de.honoka.sdk.spring.starter.config",
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class
)
@AutoConfiguration
class HonokaStarter
