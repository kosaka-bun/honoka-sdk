package de.honoka.gradle.buildsrc

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SubprojectsConfigurations(val rootProject: Project) {

    companion object {

        const val ANDROID_PROJECT_SYMBOL = "android"
    }

    private enum class ExecuteOpportunity {

        ALL, ANDROID_PROJECT, NOT_ANDROID_PROJECT
    }

    private fun defineBlock(block: Project.() -> Unit, opportunity: ExecuteOpportunity) {
        rootProject.subprojects {
            val isAndroidProject = name.lowercase().contains(ANDROID_PROJECT_SYMBOL)
            when {
                isAndroidProject && opportunity == ExecuteOpportunity.ANDROID_PROJECT -> block()
                !isAndroidProject && opportunity == ExecuteOpportunity.NOT_ANDROID_PROJECT -> block()
                opportunity == ExecuteOpportunity.ALL -> block()
                else -> {}
            }
        }
    }

    fun java(block: Project.() -> Unit) {
        defineBlock(block, ExecuteOpportunity.NOT_ANDROID_PROJECT)
    }

    fun android(block: Project.() -> Unit) {
        defineBlock(block, ExecuteOpportunity.ANDROID_PROJECT)
    }

    fun common(block: Project.() -> Unit) {
        defineBlock(block, ExecuteOpportunity.ALL)
    }
}

fun Project.subprojectsCustomConfigurations(block: SubprojectsConfigurations.() -> Unit) {
    SubprojectsConfigurations(this).block()
}
