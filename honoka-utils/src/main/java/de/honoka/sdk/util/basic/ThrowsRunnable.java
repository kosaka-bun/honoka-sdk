package de.honoka.sdk.util.basic;

import lombok.SneakyThrows;

public interface ThrowsRunnable extends Runnable {

    void throwsRun() throws Throwable;

    @SneakyThrows
    @Override
    default void run() {
        throwsRun();
    }
}
