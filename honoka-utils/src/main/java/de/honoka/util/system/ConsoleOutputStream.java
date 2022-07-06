package de.honoka.util.system;

import de.honoka.util.system.gui.ColorAttributeSets;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于代理系统输出流的自定义输出流
 */
public abstract class ConsoleOutputStream extends OutputStream {

    private final List<Byte> buffer = new ArrayList<>();

    protected final PrintStream originalPrintStream;

    private final AttributeSet defaultPrintAttributeSet;

    private AttributeSet printAttributeSet;

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

    @Override
    public synchronized void write(byte[] b, int off, int len)
            throws IOException {
        super.write(b, off, len);
        //这里在字节数组输出完成后将缓存中的字节转换为字符串然后输出
        print(buffer);
        buffer.clear();
    }

    public abstract void print(List<Byte> bytes);

    public AttributeSet getPrintAttributeSet() {
        return printAttributeSet;
    }

    public void setPrintAttributeSet(AttributeSet printAttributeSet) {
        this.printAttributeSet = printAttributeSet;
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
