package de.honoka.sdk.json.api.util;

import de.honoka.sdk.json.api.service.JsonConfigCallback;
import lombok.Getter;

@Getter
public class JsonConfig {

    private static final JsonConfig instance = new JsonConfig();

    private boolean camelCase;

    private boolean pretty;

    public static JsonConfig get() {
        return instance;
    }

    public JsonConfig setCamelCase(boolean camelCase) {
        this.camelCase = camelCase;
        JsonConfigCallback.get().onCamelCaseSet(camelCase);
        return this;
    }

    public JsonConfig setPretty(boolean pretty) {
        this.pretty = pretty;
        JsonConfigCallback.get().onPrettySet(pretty);
        return this;
    }
}
