package de.honoka.util.code;

import lombok.SneakyThrows;

public interface ThrowsRunnable extends Runnable {

    void throwsRun() throws Throwable;

    @Override
    @SneakyThrows
    default void run() {
        throwsRun();
    }
}
