import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.spring)
}

version = libs.versions.p.honoka.spring.boot.starter.get()

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = sourceCompatibility
}

honoka {
    basic {
        dependencies {
            springBootBom()
        }
    }
}

dependencies {
    api(libs.hsbs.honoka.kotlin.utils)
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")
    compileOnly("org.springframework.boot:spring-boot-starter-security")
    compileOnly(libs.mybatis.plus)
    compileOnly(libs.mybatis.plus.jsqlparser)
    val configProcessor = "org.springframework.boot:spring-boot-configuration-processor:${
        libs.versions.d.spring.boot.get()
    }"
    kapt(configProcessor)
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(java.sourceCompatibility.toString()))
        }
    }
}

honoka {
    basic {
        publishing {
            default()
        }
    }
}
