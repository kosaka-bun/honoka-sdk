package de.honoka.sdk.json.gson;

import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.service.JsonArrayService;

import java.util.Collection;

public class GsonJsonArrayService implements JsonArrayService {

    @Override
    public <T> JsonArray<T> of(Class<T> clazz) {
        return new GsonJsonArray<>(clazz);
    }

    @Override
    public <T> JsonArray<T> of(String jsonStr, Class<T> clazz) {
        return new GsonJsonArray<>(jsonStr, clazz);
    }

    @Override
    public <T> JsonArray<T> of(Collection<?> collection, Class<T> clazz) {
        return of(Common.gson.toJson(collection), clazz);
    }
}
