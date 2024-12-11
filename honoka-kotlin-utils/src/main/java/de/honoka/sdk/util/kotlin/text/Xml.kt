package de.honoka.sdk.util.kotlin.text

import de.honoka.sdk.util.text.XmlUtils
import org.dom4j.Element
import org.intellij.lang.annotations.Language

fun Element.addElementByStr(@Language("XML") str: String): Element {
    val element = XmlUtils.parseElement(str)
    add(element)
    return element
}