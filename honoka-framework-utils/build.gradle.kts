import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.Versions

setupVersionAndPublishing("1.0.2")

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${Versions.HonokaFrameworkUtils.springBootVersion}")
    }
}

dependencies {
    implementation("de.honoka.sdk:honoka-utils:1.0.8")
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    compileOnly("javax.servlet:javax.servlet-api:3.1.0")
    compileOnly("org.aspectj:aspectjweaver:1.9.4")
    compileOnly("org.hibernate:hibernate-core:5.4.4.Final")
    testImplementation("junit:junit:4.13")
}
