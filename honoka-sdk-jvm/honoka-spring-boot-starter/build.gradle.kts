import de.honoka.gradle.util.dsl.common
import de.honoka.gradle.util.dsl.libs

honoka.basic {
    publishing.version = libs.common.versions.p.honoka.spring.boot.starter.get()

    dependencies {
        springBootBom()
        springBootConfigProcessor()
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-aop")
    api("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    compileOnly(libs.mybatis.plus.spring.boot.starter)
    compileOnly(libs.mybatis.plus.jsqlparser)
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis")
    compileOnly(libs.redisson.spring.boot.starter)
    api(libs.common.honoka.kotlin.utils)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
