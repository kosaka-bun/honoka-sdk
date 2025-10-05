package de.honoka.sdk.util.gui;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

public class ConsoleInputStream extends InputStream {

    //package-private
    final Queue<Byte> buffer = new LinkedList<>();

    private final ConsoleWindow window;

    public ConsoleInputStream(ConsoleWindow window) {
        this.window = window;
    }

    @SneakyThrows
    @Override
    public int read() {
        Byte b = buffer.poll();
        if(b != null) return b.intValue();
        while(b == null) {
            synchronized(this) {
                window.showInputField();
                this.wait();
                b = buffer.poll();
            }
        }
        window.hideInputField();
        return b.intValue();
    }
}
