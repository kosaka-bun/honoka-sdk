import de.honoka.gradle.util.dsl.implementationApi

plugins {
    alias(libs.plugins.kotlin.spring)
}

version = libs.versions.p.honoka.spring.boot.starter.get()

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
    implementationApi(libs.hsbs.honoka.kotlin.utils)
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")
    compileOnly("org.springframework.boot:spring-boot-starter-security")
    compileOnly("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.5")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
}

honoka {
    basic {
        publishing {
            default()
        }
    }
}
