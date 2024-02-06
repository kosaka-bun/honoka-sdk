package de.honoka.sdk.util.android.database

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Table(

    val version: Int
)