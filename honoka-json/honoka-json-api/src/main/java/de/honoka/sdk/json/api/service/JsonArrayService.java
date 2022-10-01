package de.honoka.sdk.json.api.service;

import de.honoka.sdk.json.api.JsonArray;

public interface JsonArrayService {

    <T> JsonArray<T> of(Class<T> clazz);

    <T> JsonArray<T> of(String jsonStr, Class<T> clazz);
}
