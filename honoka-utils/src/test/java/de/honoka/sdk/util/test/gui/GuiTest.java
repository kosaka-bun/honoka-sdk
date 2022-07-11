package de.honoka.sdk.util.test.gui;

import de.honoka.sdk.util.code.CodeUtils;
import de.honoka.sdk.util.system.gui.ConsoleWindow;

import java.util.UUID;

public class GuiTest {

    public static void main(String[] args) {
        consoleWindowTest();
    }

    private static void consoleWindowTest() {
        ConsoleWindow window = new ConsoleWindow("hello", null,
                () -> {
                    System.out.println("系统退出");
                });
        window.setAutoScroll(true);
        window.setScreenZoomScale(1.25);
        window.show();
        System.out.println(1);
        System.out.println(2);
        System.err.println(3);
        System.out.println("显示一段中文");
        System.out.println("\u001B[32m :: Spring Boot :: \u001B[39m      \u001B[2m (v2.3.5.RELEASE)\u001B[0;39m");
        for(int i = 1; i <= 200; i++) {
            System.out.println(i + "\t" + UUID.randomUUID());
            CodeUtils.threadSleep(10);
        }
        new Exception().printStackTrace();
        String str = window.getText();
        System.out.println(str);
    }
}
