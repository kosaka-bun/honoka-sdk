package de.honoka.sdk.json.api.util;

import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.JsonObject;

public abstract class JsonDataConverter<T> {

    protected final T data;

    protected JsonDataConverter(T data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <T1> T1 transfer(Class<T1> type) {
        //自动推断类型
        if(type == null) return (T1) toUnknownType();
        //指定类型
        if(type == String.class) return (T1) castToString();
        if(type == Integer.class) return (T1) toInt();
        if(type == Long.class) return (T1) toLong();
        if(type == Double.class) return (T1) toDouble();
        if(type == Boolean.class) return (T1) toBoolean();
        if(type == Byte.class) return (T1) toByte();
        if(type == Character.class) return (T1) toChar();
        if(type == JsonObject.class) return (T1) toJsonObject();
        if(type == JsonArray.class) return (T1) toJsonArray();
        //其他类型
        return toObject(type);
    }

    protected abstract boolean isJsonObject();

    protected abstract boolean isJsonArray();

    protected abstract JsonObject toJsonObject();

    protected JsonArray<?> toJsonArray() {
        return toJsonArray(null);
    }

    protected abstract <T1> JsonArray<T1> toJsonArray(Class<T1> type);

    protected abstract Integer toInt();

    protected abstract Long toLong();

    protected abstract Double toDouble();

    protected abstract Boolean toBoolean();

    protected abstract String castToString();

    protected abstract Byte toByte();

    protected abstract Character toChar();

    protected abstract <T1> T1 toObject(Class<T1> type);

    protected abstract Object toUnknownType();
}
