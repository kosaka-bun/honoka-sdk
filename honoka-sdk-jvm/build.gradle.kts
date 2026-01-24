import de.honoka.gradle.util.dsl.classifyProjects
import de.honoka.gradle.util.dsl.projects
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

plugins {
    java
    `java-library`
    `maven-publish`
    alias(commonLibs.plugins.kotlin)
    alias(commonLibs.plugins.kotlin.kapt)
    alias(commonLibs.plugins.kotlin.lombok)
    alias(commonLibs.plugins.honoka.basic)
}

group = "de.honoka.sdk"
version = commonLibs.versions.p.root.get()

val projects = classifyProjects {
    java8 = subprojects - projects("honoka-spring-boot-starter")
    kotlin = subprojects - projects("honoka-utils")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "de.honoka.gradle.plugin.basic")

    group = rootProject.group

    java {
        if(project in projects.java8) {
            toolchain.languageVersion = JavaLanguageVersion.of(8)
        }
        withSourcesJar()
    }

    honoka.basic {
        dependencies {
            lombok()
        }
    }
    
    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }
    
    if(project in projects.kotlin) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "org.jetbrains.kotlin.kapt")
        apply(plugin = "org.jetbrains.kotlin.plugin.lombok")

        honoka.basic {
            dependencies {
                kotlin()
            }
        }

        tasks {
            /*
             * 由于除了原本的compileKotlin任务外，还存在compileTestKotlin和kapt的KaptGenerateStubsTask
             * （KotlinCompile的子类）任务需要配置，因此这里不能使用“compileKotlin {}”块。
             */
            withType<KotlinCompile> {
                compilerOptions {
                    freeCompilerArgs.addAll("-Xjsr305=strict", "-Xjvm-default=all")
                }
            }
        }

        kapt {
            keepJavacAnnotationProcessors = true
        }
    }

    tasks {
        withType<JavaCompile> {
            options.run {
                encoding = StandardCharsets.UTF_8.name()
                val compilerArgs = compilerArgs as MutableCollection<String>
                compilerArgs += listOf("-parameters")
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }
    }
}

honoka.basic {
    publishing {
        defineCheckVersionTask()
    }
}
