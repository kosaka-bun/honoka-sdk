import de.honoka.gradle.buildsrc.MavenPublish.defineCheckVersionOfProjectsTask
import de.honoka.gradle.buildsrc.kotlin
import de.honoka.gradle.buildsrc.projects
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    `java-library`
    `maven-publish`
    alias(libs.plugins.dependency.management)
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.lombok) apply false
}

group = "de.honoka.sdk"
version = libs.versions.root.get()

//纯Java项目
val javaProjects = projects("honoka-utils")

//非Java 8项目
val notJava8Projects = projects("honoka-spring-boot-starter")

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "io.spring.dependency-management")

    group = rootProject.group

    java {
        if(project !in notJava8Projects) {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = sourceCompatibility
        }
        withSourcesJar()
    }
    
    dependencies {
        rootProject.libs.lombok.let {
            compileOnly(it)
            annotationProcessor(it)
            testCompileOnly(it)
            testAnnotationProcessor(it)
        }
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }
    
    //Kotlin项目
    if(project !in javaProjects) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "org.jetbrains.kotlin.kapt")
        apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
        dependencyManagement {
            imports {
                mavenBom(rootProject.libs.kotlin.bom.get().toString())
            }
        }
        dependencies {
            kotlin(project)
            //仅用于避免libs.versions.toml中产生version变量未使用的提示
            rootProject.libs.versions.kotlin.coroutines
        }
        tasks {
            withType<KotlinCompile> {
                kotlinOptions {
                    jvmTarget = java.sourceCompatibility.toString()
                    freeCompilerArgs += listOf(
                        "-Xjsr305=strict",
                        "-Xjvm-default=all"
                    )
                }
            }
        }
        kapt {
            keepJavacAnnotationProcessors = true
        }
    }

    tasks {
        compileJava {
            options.run {
                encoding = StandardCharsets.UTF_8.name()
                val compilerArgs = compilerArgs as MutableCollection<String>
                compilerArgs += listOf(
                    "-parameters"
                )
            }
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
