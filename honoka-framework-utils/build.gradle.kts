import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing

setupVersionAndPublishing(libs.versions.honoka.framework.utils.get())

dependencyManagement {
    imports {
        mavenBom(libs.spring.boot.dependencies.get().toString())
    }
}

dependencies {
    implementation("de.honoka.sdk:honoka-utils:1.0.10")
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    compileOnly("javax.servlet:javax.servlet-api:3.1.0")
    compileOnly("org.aspectj:aspectjweaver:1.9.4")
    compileOnly("org.hibernate:hibernate-core:5.4.4.Final")
    testImplementation("junit:junit:4.13")
}

tasks {
    compileJava {
        dependsOn(":honoka-utils:publish")
    }
}