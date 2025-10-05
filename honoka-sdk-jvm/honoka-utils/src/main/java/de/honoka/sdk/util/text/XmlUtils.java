package de.honoka.sdk.util.text;

import lombok.SneakyThrows;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.intellij.lang.annotations.Language;

import java.io.StringReader;

public class XmlUtils {

    @SneakyThrows
    public static Document read(@Language("XML") String xmlStr) {
        return new SAXReader().read(new StringReader(xmlStr));
    }

    public static Element readRootElement(@Language("XML") String xmlStr) {
        return read(xmlStr).getRootElement();
    }

    @SneakyThrows
    public static Element parseElement(@Language("XML") String str) {
        Element rootElement = DocumentHelper.parseText(str).getRootElement();
        rootElement.setDocument(null);
        return rootElement;
    }
}
