package de.honoka.sdk.util.kotlin.basic

/*
 * 注意：使用该类进行逻辑运算时，会影响逻辑运算的短路规则，仅适用于对短路规则没有硬性要求的运算。
 */
class BooleanBuilder(private var boolean: Boolean = false) {
    
    companion object {
        
        inline fun calc(block: BooleanBuilder.() -> Boolean): Boolean = BooleanBuilder().run(block)
    }
    
    private fun Boolean.save(): Boolean = also {
        boolean = it
    }
    
    fun init(value: Boolean): Boolean = value.save()
    
    fun and(value: Boolean): Boolean = (boolean && value).save()
    
    fun or(value: Boolean): Boolean = (boolean || value).save()
}
