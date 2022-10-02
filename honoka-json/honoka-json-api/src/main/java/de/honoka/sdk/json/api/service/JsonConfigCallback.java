package de.honoka.sdk.json.api.service;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ServiceLoader;

public interface JsonConfigCallback {

    void onCamelCaseSet(boolean camelCase);

    void onPrettySet(boolean pretty);

    static JsonConfigCallback get() {
        ServiceLoader<JsonConfigCallback> loader = ServiceLoader.load(
                JsonConfigCallback.class);
        for(JsonConfigCallback callback : loader) {
            return callback;
        }
        throw new NotImplementedException(JsonConfigCallback.class.getName());
    }
}
