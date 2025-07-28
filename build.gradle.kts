import de.honoka.gradle.plugin.basic.ext.DependenciesDsl.kotlin
import de.honoka.gradle.plugin.basic.ext.MavenPublishDsl.defineCheckVersionTask
import de.honoka.gradle.util.dsl.projects
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

plugins {
    java
    `java-library`
    `maven-publish`
    alias(libs.plugins.dependency.management)
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.lombok) apply false
    alias(libs.plugins.honoka.basic)
}

group = "de.honoka.sdk"
version = libs.versions.p.root.get()

//纯Java项目
val javaProjects = projects("honoka-utils")

//非Java 8项目
val notJava8Projects = projects("honoka-spring-boot-starter")

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "de.honoka.gradle.plugin.basic")

    val libs = rootProject.libs

    group = rootProject.group

    java {
        if(project !in notJava8Projects) {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = sourceCompatibility
        }
        withSourcesJar()
    }
    
    dependencies {
        libs.lombok.let {
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
                mavenBom(libs.kotlin.bom.get().toString())
            }
        }
        dependencies {
            kotlin()
            //仅用于避免libs.versions.toml中产生version变量未使用的提示
            libs.versions.d.kotlin.coroutines
        }
        tasks {
            withType<KotlinCompile> {
                kotlinOptions {
                    jvmTarget = java.sourceCompatibility.toString()
                    freeCompilerArgs += listOf("-Xjsr305=strict", "-Xjvm-default=all")
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
                compilerArgs += listOf("-parameters")
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

publishing {
    defineCheckVersionTask()
}
