package de.honoka.sdk.util.text;

import lombok.SneakyThrows;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.StringReader;

public class XmlUtils {

    @SneakyThrows
    public static Document read(String xmlStr) {
        return new SAXReader().read(new StringReader(xmlStr));
    }
}
