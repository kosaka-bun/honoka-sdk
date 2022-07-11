package de.honoka.sdk.util.test.gui;

import de.honoka.sdk.util.code.ColorfulText;
import de.honoka.sdk.util.system.gui.ConsoleWindow;
import lombok.SneakyThrows;

import java.util.Scanner;

public class GuiTest2 {

    @SneakyThrows
    public static void main(String[] args) {
        ConsoleWindow window = new ConsoleWindow("hello",
                null, () -> {});
        window.show();
        Thread.sleep(1000);
        String str = new Scanner(System.in).nextLine();
        ColorfulText.of().green(str).println();
        Thread.sleep(3000);
        System.exit(0);
    }
}
