import de.honoka.gradle.util.dsl.*

plugins {
    alias(commonLibs.plugins.kotlin.spring)
}

honoka.basic.publishing.version = libs.common.versions.p.honoka.spring.boot.starter.get()

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
    api(libs.common.honoka.kotlin.utils)
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")
    compileOnly("org.springframework.boot:spring-boot-starter-security")
    compileOnly(libs.mybatis.plus.spring.boot.starter)
    compileOnly(libs.mybatis.plus.jsqlparser)
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis")
    compileOnly(libs.redisson.spring.boot.starter)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
