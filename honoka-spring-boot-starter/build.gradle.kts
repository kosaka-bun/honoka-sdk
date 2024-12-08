import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.implementationApi

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.spring)
}

setupVersionAndPublishing(libs.versions.honoka.spring.boot.starter.get())

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = sourceCompatibility
}

dependencyManagement {
    imports {
        //必须按照顺序导入，后导入的依赖配置将覆盖先导入的相同依赖的配置
        mavenBom(libs.spring.boot.dependencies.get().toString())
        mavenBom(libs.kotlin.bom.get().toString())
    }
}

dependencies {
    implementationApi("de.honoka.sdk:honoka-kotlin-utils:1.0.1")
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")
    compileOnly("org.springframework.boot:spring-boot-starter-security")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor".also {
        annotationProcessor(it)
    })
}

tasks {
    compileKotlin {
        dependsOn(":honoka-kotlin-utils:publish")
    }
}