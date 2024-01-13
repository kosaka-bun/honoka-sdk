plugins {
    java
    `maven-publish`
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "de.honoka.sdk"
version = "1.1.7"

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "io.spring.dependency-management")

    group = rootProject.group

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        withSourcesJar()
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.26".also {
            annotationProcessor(it)
            testCompileOnly(it)
            testAnnotationProcessor(it)
        })
        //Test
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
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