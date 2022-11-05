package de.honoka.sdk.json.api.service;

import de.honoka.sdk.json.api.JsonObject;

public interface JsonObjectService {

    JsonObject of();

    JsonObject of(String jsonStr);

    JsonObject of(Object obj);
}
