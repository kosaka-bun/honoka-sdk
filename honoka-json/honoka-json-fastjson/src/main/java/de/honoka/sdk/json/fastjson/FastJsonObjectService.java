package de.honoka.sdk.json.fastjson;

import de.honoka.sdk.json.api.JsonObject;
import de.honoka.sdk.json.api.service.JsonObjectService;

public class FastJsonObjectService implements JsonObjectService {

    @Override
    public JsonObject of() {
        return new FastJsonObject();
    }

    @Override
    public JsonObject of(String jsonStr) {
        return new FastJsonObject(jsonStr);
    }
}
