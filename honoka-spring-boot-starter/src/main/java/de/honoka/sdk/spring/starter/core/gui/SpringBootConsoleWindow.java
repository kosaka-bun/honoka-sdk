package de.honoka.sdk.spring.starter.core.gui;

import de.honoka.sdk.util.code.ThrowsRunnable;
import de.honoka.sdk.util.gui.ConsoleWindow;
import de.honoka.sdk.util.gui.ConsoleWindowBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.function.Consumer;

@Getter
@Setter
@Accessors(chain = true)
public class SpringBootConsoleWindow {

    @Setter(AccessLevel.NONE)
    private ConsoleWindow consoleWindow;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ConsoleWindowBuilder consoleWindowBuilder;

    private Class<?> springBootMainClass;

    private String[] applicationArgs = new String[0];

    private ThrowsRunnable beforeRunApplication = () -> {};

    private Consumer<ApplicationContext> onExit = context -> {};

    @Setter(AccessLevel.NONE)
    private ApplicationContext context;

    private SpringBootConsoleWindow() {}

    @SuppressWarnings("SameParameterValue")
    public static SpringBootConsoleWindow of(
        String windowName, double screenZoomScale, Class<?> mainClass, String[] args
    ) {
        SpringBootConsoleWindow springBootConsoleWindow = new SpringBootConsoleWindow();
        springBootConsoleWindow.consoleWindowBuilder = ConsoleWindow.Builder.of()
                .setWindowName(windowName)
                .setScreenZoomScale(screenZoomScale)
                .setBackgroundMode(true)
                .setOnExit(() -> springBootConsoleWindow.onExit.accept(springBootConsoleWindow.context));
        springBootConsoleWindow.springBootMainClass = mainClass;
        springBootConsoleWindow.applicationArgs = args;
        return springBootConsoleWindow;
    }

    public static SpringBootConsoleWindow of(String windowName, double screenZoomScale, Class<?> mainClass) {
        return of(windowName, screenZoomScale, mainClass, new String[0]);
    }

    public static SpringBootConsoleWindow of(double screenZoomScale, Class<?> mainClass) {
        return of(mainClass.getSimpleName(), screenZoomScale, mainClass);
    }

    public static SpringBootConsoleWindow of(Class<?> mainClass) {
        return of(1.0, mainClass);
    }

    public SpringBootConsoleWindow configureWindowBuilder(Consumer<ConsoleWindowBuilder> configurer) {
        configurer.accept(consoleWindowBuilder);
        return this;
    }

    /**
     * 创建窗口，并启动SpringBoot应用
     */
    public void createAndRun() {
        consoleWindow = consoleWindowBuilder.build();
        beforeRunApplication.run();
        SpringApplication app = new SpringApplication(springBootMainClass);
        context = app.run(applicationArgs);
    }
}
