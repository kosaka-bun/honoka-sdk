import de.honoka.gradle.buildsrc.MavenPublish.defineCheckVersionOfProjectsTask
import de.honoka.gradle.buildsrc.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    `java-library`
    `maven-publish`
    alias(libs.plugins.dependency.management)
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.lombok) apply false
}

group = "de.honoka.sdk"
version = libs.versions.root.get()

//纯Java项目的名称列表
val javaProjectNames = listOf("honoka-utils")
val javaProjects = javaProjectNames.map { project(":$it") }

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "io.spring.dependency-management")

    group = rootProject.group

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = sourceCompatibility
        withSourcesJar()
    }
    
    dependencies {
        compileOnly(rootProject.libs.lombok.also {
            annotationProcessor(it)
            testCompileOnly(it)
            testAnnotationProcessor(it)
        })
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }
    
    //Kotlin项目
    if(project !in javaProjects) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
        dependencyManagement {
            imports {
                mavenBom(rootProject.libs.kotlin.bom.get().toString())
            }
        }
        dependencies {
            kotlin(rootProject)
            //仅用于避免libs.versions.toml中产生version变量未使用的提示
            rootProject.libs.versions.kotlin.coroutines
        }
        tasks {
            withType<KotlinCompile> {
                kotlinOptions {
                    freeCompilerArgs += "-Xjsr305=strict"
                    jvmTarget = java.sourceCompatibility.toString()
                }
            }
        }
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
        }
    }
}

defineCheckVersionOfProjectsTask()