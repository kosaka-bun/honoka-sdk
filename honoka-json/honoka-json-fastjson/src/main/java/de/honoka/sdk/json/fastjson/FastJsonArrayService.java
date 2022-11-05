package de.honoka.sdk.json.fastjson;

import com.alibaba.fastjson.JSON;
import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.service.JsonArrayService;

import java.util.Collection;

public class FastJsonArrayService implements JsonArrayService {

    @Override
    public <T> JsonArray<T> of(Class<T> clazz) {
        return new FastJsonArray<>(clazz);
    }

    @Override
    public <T> JsonArray<T> of(String jsonStr, Class<T> clazz) {
        return new FastJsonArray<>(jsonStr, clazz);
    }

    @Override
    public <T> JsonArray<T> of(Collection<?> collection, Class<T> clazz) {
        return of(JSON.toJSONString(collection), clazz);
    }
}
