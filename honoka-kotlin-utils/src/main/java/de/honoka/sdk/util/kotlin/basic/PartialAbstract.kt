package de.honoka.sdk.util.kotlin.basic

/**
 * “部分抽象”设计模式。
 *
 * 扩展函数仅当其被定义在包中或单例对象中时，才可以在别处被导入使用。若一个单例对象中包含需要不同依赖者
 * 各自进行实现的部分，又包含与该对象高度相关的扩展函数，则可以实现此抽象类以引入一个“部分抽象”结构。
 *
 * 在上述情况中，若直接将上述单例对象改为抽象类，则在依赖者自定义单例对象实现这一抽象类之前，类中的所有
 * 扩展函数都将无法在其他类中被直接调用，即便在其他类定义一个类型为该抽象类的属性，也依旧如此。
 */
@Suppress("MemberVisibilityCanBePrivate")
interface PartialAbstract<T : Any> {

    var abstractPart: T?
        get() = WeakReferenceContainer.get(::abstractPart)
        set(value) = WeakReferenceContainer.set(::abstractPart, value)

    fun initAbstractPart(abstractPart: T) {
        this.abstractPart = abstractPart
    }
}
