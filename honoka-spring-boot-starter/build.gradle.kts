plugins {
    alias(libs.plugins.kotlin.spring)
}

version = libs.versions.p.honoka.spring.boot.starter.get()

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
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

honoka {
    basic {
        publishing {
            default()
        }
    }
}
