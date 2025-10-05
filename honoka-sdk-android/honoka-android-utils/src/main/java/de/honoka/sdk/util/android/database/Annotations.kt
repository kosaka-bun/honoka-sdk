package de.honoka.sdk.util.android.database

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Table(

    val version: Int
)
