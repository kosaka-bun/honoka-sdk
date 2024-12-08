package de.honoka.sdk.util.gui;

import de.honoka.sdk.util.code.ThrowsRunnable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.*;
import java.net.URL;

/**
 * ConsoleWindow初始化时的默认参数
 */
@Getter
@Setter
@Accessors(chain = true)
public class ConsoleWindowBuilder {

    private String windowName = "Untitled";

    private boolean showOnBuild = true;

    private double screenZoomScale = 1.0;

    private int windowWidth = 1000;

    private int windowHeight = 600;

    private String menuItemFontName = "Microsoft YaHei UI";

    private int menuItemFontStyle = Font.PLAIN;

    private int menuItemFontSize = 12;

    private String textPaneFontName = "Microsoft YaHei Mono";

    private int textPaneFontStyle = Font.PLAIN;

    private int textPaneFontSize = 18;

    private int textPaneMaxLine = 200;

    @Getter(AccessLevel.NONE)
    private final Dimension defaultTrayIconMenuLocationOffset =
            new Dimension(21, 17);

    @Setter(AccessLevel.NONE)
    private Dimension trayIconMenuLocationOffset = new Dimension(
            defaultTrayIconMenuLocationOffset.width,
            defaultTrayIconMenuLocationOffset.height
    );

    /**
     * 是否需要创建托盘图标
     */
    private boolean backgroundMode = false;

    private URL trayIconPath;

    private ThrowsRunnable onExit;

    private ConsoleWindowBuilder() {}

    //package-private
    static ConsoleWindowBuilder of() {
        return new ConsoleWindowBuilder();
    }

    //package-private
    static ConsoleWindowBuilder of(String windowName) {
        return new ConsoleWindowBuilder().setWindowName(windowName);
    }

    /**
     * width：为负表示左移，为正表示右移
     * height：为负表示上移，为正表示下移
     */
    public ConsoleWindowBuilder setTrayIconMenuLocationOffset(int width, int height) {
        width = defaultTrayIconMenuLocationOffset.width - width;
        height = defaultTrayIconMenuLocationOffset.height - height;
        trayIconMenuLocationOffset = new Dimension(width, height);
        return this;
    }

    public ConsoleWindow build() {
        //注入参数
        ConsoleWindow consoleWindow = new ConsoleWindow();
        consoleWindow.windowName = windowName;
        consoleWindow.screenZoomScale = screenZoomScale;
        consoleWindow.defaultFrameSize = new Dimension(windowWidth, windowHeight);
        consoleWindow.menuItemFont = new Font(menuItemFontName,
                menuItemFontStyle, menuItemFontSize);
        consoleWindow.textPaneFont = new Font(textPaneFontName,
                textPaneFontStyle, textPaneFontSize);
        consoleWindow.textPaneMaxLine = textPaneMaxLine;
        consoleWindow.trayIconMenuLocationOffset = trayIconMenuLocationOffset;
        //构建
        if(backgroundMode) {
            consoleWindow.initBackgroundMode(trayIconPath, onExit);
            if(showOnBuild) consoleWindow.show();
        } else {
            if(onExit == null) consoleWindow.init();
            else consoleWindow.init(onExit);
            consoleWindow.show();
        }
        return consoleWindow;
    }
}
