package de.honoka.sdk.spring.starter.core

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
interface SimpleValidator<A : Annotation, T> : ConstraintValidator<A, T> {

    private class FailedException(

        val properties: Array<out KProperty<*>>,

        override val message: String
    ) : RuntimeException()

    fun T.isValid()

    override fun isValid(value: T, context: ConstraintValidatorContext): Boolean {
        try {
            value.isValid()
        } catch(e: FailedException) {
            context.disableDefaultConstraintViolation()
            e.properties.forEach {
                context.buildConstraintViolationWithTemplate(e.message)
                    .addPropertyNode(it.name).addConstraintViolation()
            }
            return false
        }
        return true
    }

    fun fail(message: String, vararg properies: KProperty<*>): Nothing {
        throw FailedException(properies, message)
    }
}
