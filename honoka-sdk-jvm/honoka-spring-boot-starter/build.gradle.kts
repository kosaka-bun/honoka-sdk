plugins {
    alias(commonLibs.plugins.kotlin.spring)
}

version = commonLibs.versions.p.honoka.spring.boot.starter.get()

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

honoka.basic {
    dependencies {
        springBootBom()
        springBootConfigProcessor()
    }
}

dependencies {
    api(commonLibs.honoka.kotlin.utils)
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")
    compileOnly("org.springframework.boot:spring-boot-starter-security")
    compileOnly(libs.mybatis.plus)
    compileOnly(libs.mybatis.plus.jsqlparser)
}

honoka.basic {
    publishing {
        default()
    }
}
