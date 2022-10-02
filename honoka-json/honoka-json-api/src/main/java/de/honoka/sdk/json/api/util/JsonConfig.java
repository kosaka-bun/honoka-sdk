package de.honoka.sdk.json.api.util;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ServiceLoader;

public interface JsonConfig {

    boolean isCamelCase();

    JsonConfig setCamelCase(boolean camelCase);

    boolean isPretty();

    JsonConfig setPretty(boolean pretty);

    static JsonConfig get() {
        ServiceLoader<JsonConfig> loader = ServiceLoader.load(JsonConfig.class);
        for(JsonConfig config : loader) {
            return config;
        }
        throw new NotImplementedException(JsonConfig.class.getName());
    }
}
