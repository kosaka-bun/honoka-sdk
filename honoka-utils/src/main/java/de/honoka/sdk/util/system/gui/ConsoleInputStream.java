package de.honoka.sdk.util.system.gui;

import javax.swing.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class ConsoleInputStream extends InputStream {

    private final Queue<Byte> buffer = new LinkedList<>();

    private final ConsoleWindow window;

    public ConsoleInputStream(ConsoleWindow window) {
        this.window = window;
    }

    @Override
    public int read() {
        Byte b = buffer.poll();
        if(b != null) return b.intValue();
        String str = JOptionPane.showInputDialog(null,
                "请输入传递给控制台的内容：", window.windowName,
                JOptionPane.INFORMATION_MESSAGE);
        str = str == null ? "\n" : str + "\n";
        window.writeToTextPane(str, null);
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        for(byte aByte : bytes) {
            buffer.offer(aByte);
        }
        buffer.offer((byte) -1);
        return Objects.requireNonNull(buffer.poll()).intValue();
    }
}
