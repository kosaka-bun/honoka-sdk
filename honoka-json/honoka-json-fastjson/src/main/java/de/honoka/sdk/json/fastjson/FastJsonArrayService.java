package de.honoka.sdk.json.fastjson;

import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.service.JsonArrayService;

class FastJsonArrayService implements JsonArrayService {

    @Override
    public <T> JsonArray<T> of(Class<T> clazz) {
        return new FastJsonArray<>(clazz);
    }

    @Override
    public <T> JsonArray<T> of(String jsonStr, Class<T> clazz) {
        return new FastJsonArray<>(jsonStr, clazz);
    }
}
