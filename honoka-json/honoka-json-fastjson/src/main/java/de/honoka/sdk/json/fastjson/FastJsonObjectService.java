package de.honoka.sdk.json.fastjson;

import com.alibaba.fastjson.JSON;
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

    @Override
    public JsonObject of(Object obj) {
        return of(JSON.toJSONString(obj));
    }
}
