import de.honoka.gradle.buildsrc.MavenPublish.setupVersionAndPublishing
import de.honoka.gradle.buildsrc.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin)
}

setupVersionAndPublishing(libs.versions.honoka.framework.utils.get())

dependencyManagement {
    imports {
        mavenBom(libs.spring.boot.dependencies.get().toString())
    }
}

dependencies {
    kotlin(project)
    arrayOf(
        "de.honoka.sdk:honoka-kotlin-utils:1.0.1-dev"
    ).forEach {
        implementation(it)
        api(it)
    }
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    compileOnly("javax.servlet:javax.servlet-api:3.1.0")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("org.aspectj:aspectjweaver:1.9.4")
    compileOnly("org.hibernate:hibernate-core:5.4.4.Final")
    testImplementation("junit:junit:4.13")
}

tasks {
    compileJava {
        dependsOn(":honoka-kotlin-utils:publish")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = java.sourceCompatibility.toString()
        }
    }
}