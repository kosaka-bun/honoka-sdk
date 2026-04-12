package de.honoka.sdk.util.ui;

import com.formdev.flatlaf.FlatLightLaf;
import de.honoka.sdk.util.various.CodeUtils;

import javax.swing.*;

public class UiUtils {

    public static void setUiStyleWithCurrentOs() {
        //noinspection CodeBlock2Expr
        CodeUtils.runCatching(() -> {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        });
        FlatLightLaf.setup();
    }
}
