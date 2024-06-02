import de.honoka.gradle.buildsrc.MavenPublish.defineCheckVersionOfProjectsTask
import java.nio.charset.StandardCharsets

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    `java-library`
    `maven-publish`
    alias(libs.plugins.dependency.management) apply false
}

group = "de.honoka.sdk"
version = libs.versions.root.get()

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
        val libs = rootProject.libs
        compileOnly(libs.lombok.also {
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