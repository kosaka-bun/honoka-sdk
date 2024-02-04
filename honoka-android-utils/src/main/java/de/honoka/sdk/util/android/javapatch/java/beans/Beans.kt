@file:Suppress("PackageDirectoryMismatch")

package java.beans

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Transient(

    val value: Boolean = true
)