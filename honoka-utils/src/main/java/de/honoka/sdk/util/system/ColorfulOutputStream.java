package de.honoka.sdk.util.system;

import de.honoka.sdk.util.code.ActionUtils;
import de.honoka.sdk.util.system.gui.ColorAttributeSets;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ColorfulOutputStream extends OutputStream {

    private final List<Byte> buffer = new ArrayList<>();

    protected final PrintStream originalPrintStream;

    private final Color defaultPrintColor;

    @Getter
    @Setter
    private Color printColor;

    @Setter
    private Consumer<byte[]> printMethod;

    public ColorfulOutputStream(PrintStream originalPrintStream,
                                Color defaultPrintColor) {
        this.originalPrintStream = originalPrintStream;
        this.defaultPrintColor = defaultPrintColor;
        printColor = defaultPrintColor;
    }

    @Override
    public void write(int b) {
        buffer.add((byte) b);
        originalPrintStream.write(b);
    }

    @SneakyThrows
    @Override
    public synchronized void write(byte[] b, int off, int len) {
        super.write(b, off, len);
        //这里在字节数组输出完成后将缓存中的字节传递给消费者
        printMethod.accept(ArrayUtils.toPrimitive(buffer.toArray(new Byte[0])));
        buffer.clear();
    }

    public String changePrintColorByAnsiString(String str) {
        if(!str.startsWith("[") || !str.contains("m")) return str;
        String ansi = str.substring(str.indexOf("[") + 1, str.indexOf("m"));
        List<String> parts = Arrays.asList(ansi.split(";"));
        parts.forEach(s -> {
            if(s.equals("0")) {
                printColor = defaultPrintColor;
                return;
            }
            ActionUtils.doIgnoreException(() -> {
                int ansiCode = Integer.parseInt(s);
                Color color = ColorAttributeSets.getColor(ansiCode);
                if(color != null) {
                    printColor = color;
                }
            });
        });
        return str.substring(str.indexOf("m") + 1);
    }
}
