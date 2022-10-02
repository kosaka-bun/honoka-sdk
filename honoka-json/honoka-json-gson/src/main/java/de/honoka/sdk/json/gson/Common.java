package de.honoka.sdk.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.JsonObject;
import de.honoka.sdk.json.api.service.JsonConfigCallback;
import de.honoka.sdk.json.api.util.JsonConfig;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;

//package-private
class Common {

    static GsonBuilder gsonBuilder = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls();

    static Gson gson;

    static {
        init();
    }

    static void init() {
        JsonConfig config = JsonConfig.get();
        JsonConfigCallback callback = JsonConfigCallback.get();
        callback.onPrettySet(config.isPretty());
        callback.onCamelCaseSet(config.isCamelCase());
    }

    static void buildGson() {
        gson = gsonBuilder.create();
    }

    @SneakyThrows
    static GsonBuilder copyBuilder() {
        Constructor<GsonBuilder> constructor = GsonBuilder.class
                .getDeclaredConstructor(Gson.class);
        constructor.setAccessible(true);
        return constructor.newInstance(gson);
    }

    static com.google.gson.JsonElement toOriginalJsonElement(Object value) {
        //本框架的Json数据
        if(value instanceof GsonJsonObject)
            return ((GsonJsonObject) value).originalJsonObject;
        if(value instanceof GsonJsonArray)
            return ((GsonJsonArray<?>) value).originalJsonArray;
        //其他框架的Json数据
        if(value instanceof JsonObject || value instanceof JsonArray)
            return com.google.gson.JsonParser.parseString(value.toString());
        //其他类型
        return com.google.gson.JsonParser.parseString(gson.toJson(value));
    }
}
