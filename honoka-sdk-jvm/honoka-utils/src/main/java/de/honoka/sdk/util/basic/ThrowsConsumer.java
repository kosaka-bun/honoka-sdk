package de.honoka.sdk.util.basic;

import lombok.SneakyThrows;

import java.util.function.Consumer;

public interface ThrowsConsumer<T> extends Consumer<T> {

    void throwsAccept(T t) throws Throwable;

    @SneakyThrows
    @Override
    default void accept(T t) {
        throwsAccept(t);
    }
}
