import de.honoka.gradle.buildsrc.MavenPublish.defineCheckVersionOfProjectsTask
import de.honoka.gradle.buildsrc.Versions
import java.nio.charset.StandardCharsets

plugins {
    java
    `java-library`
    `maven-publish`
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
}

group = "de.honoka.sdk"
version = "1.3.0"

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "io.spring.dependency-management")

    group = rootProject.group

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = sourceCompatibility
        withSourcesJar()
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:${Versions.lombok}".also {
            annotationProcessor(it)
            testCompileOnly(it)
            testAnnotationProcessor(it)
        })
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }

    tasks {
        compileJava {
            options.encoding = StandardCharsets.UTF_8.name()
        }

        test {
            useJUnitPlatform()
        }
    }

    publishing {
        repositories {
            mavenLocal()
            if(hasProperty("remoteMavenRepositoryUrl")) {
                maven(properties["remoteMavenRepositoryUrl"]!!)
            }
        }
    }
}

defineCheckVersionOfProjectsTask()