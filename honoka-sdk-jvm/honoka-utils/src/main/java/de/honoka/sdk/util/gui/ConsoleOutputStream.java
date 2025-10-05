package de.honoka.sdk.util.gui;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * 用于代理系统输出流的自定义输出流
 */
public class ConsoleOutputStream extends OutputStream {

    private final List<Byte> buffer = new ArrayList<>();

    protected final PrintStream originalPrintStream;

    private final AttributeSet defaultPrintAttributeSet;

    @Getter
    @Setter
    private AttributeSet printAttributeSet;

    @Setter
    private Consumer<List<Byte>> printMethod;

    public ConsoleOutputStream(PrintStream systemPrintStream, Color printColor) {
        this.originalPrintStream = systemPrintStream;
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, printColor);
        defaultPrintAttributeSet = attributeSet;
        printAttributeSet = attributeSet;
    }

    @Override
    public void write(int b) {
        buffer.add((byte) b);
        originalPrintStream.write(b);
    }

    @SuppressWarnings("NullableProblems")
    @SneakyThrows
    @Override
    public synchronized void write(byte[] b, int off, int len) {
        super.write(b, off, len);
        //这里在字节数组输出完成后将缓存中的字节转换为字符串然后输出
        printMethod.accept(buffer);
        buffer.clear();
    }

    public String changePrintColorByAnsiString(String str) {
        if(!str.startsWith("[") || !str.contains("m")) return str;
        String ansi = str.substring(str.indexOf("[") + 1, str.indexOf("m"));
        List<String> parts = Arrays.asList(ansi.split(";"));
        parts.forEach(s -> {
            if(s.equals("0")) {
                setPrintAttributeSet(defaultPrintAttributeSet);
                return;
            }
            try {
                int ansiCode = Integer.parseInt(s);
                AttributeSet attributeSet = ColorAttributeSets
                        .getAttributeSet(ansiCode);
                if(attributeSet != null) setPrintAttributeSet(attributeSet);
            } catch(Exception e) {
                //none
            }
        });
        return str.substring(str.indexOf("m") + 1);
    }
}
