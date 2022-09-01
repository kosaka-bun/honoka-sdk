package de.honoka.sdk.util.test.gui;

import de.honoka.sdk.util.code.ColorfulText;
import de.honoka.sdk.util.system.gui.ConsoleWindow;
import lombok.SneakyThrows;

import java.util.Scanner;

public class GuiTest2 {

    @SneakyThrows
    public static void main(String[] args) {
        ConsoleWindow window = new ConsoleWindow("hello");
        window.setScreenZoomScale(1.25);
        window.show();
        Thread.sleep(1000);
        System.out.print("请输入：");
        String str = new Scanner(System.in).nextLine();
        ColorfulText.of().green(str).println();
        Thread.sleep(3000);
        System.exit(0);
    }
}
