package de.honoka.sdk.json.gson;

import de.honoka.sdk.json.api.JsonObject;
import de.honoka.sdk.json.api.service.JsonObjectService;

public class GsonJsonObjectService implements JsonObjectService {

    @Override
    public JsonObject of() {
        return new GsonJsonObject();
    }

    @Override
    public JsonObject of(String jsonStr) {
        return new GsonJsonObject(jsonStr);
    }
}
