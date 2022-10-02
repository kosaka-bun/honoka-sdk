package de.honoka.sdk.json.gson;

import de.honoka.sdk.json.api.util.JsonConfig;
import lombok.Getter;

@Getter
public class GsonJsonConfig implements JsonConfig {

    private boolean camelCase;

    private boolean pretty;

    @Override
    public JsonConfig setCamelCase(boolean camelCase) {
        this.camelCase = camelCase;
        Common.init();
        return this;
    }

    @Override
    public JsonConfig setPretty(boolean pretty) {
        this.pretty = pretty;
        Common.init();
        return this;
    }
}
