package de.honoka.sdk.util.kotlin.text

import org.dom4j.Document
import org.dom4j.io.SAXReader
import java.io.StringReader

object XmlUtils {

    fun read(xmlStr: String): Document = SAXReader().read(StringReader(xmlStr))
}