package de.honoka.sdk.json.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.JsonObject;
import de.honoka.sdk.json.api.util.JsonConfig;

//package-private
class Common {

    static GsonBuilder gsonBuilder;

    static Gson gson;

    static {
        init();
    }

    static void init() {
        JsonConfig config = JsonConfig.get();
        gsonBuilder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls();
        if(config.isPretty()) {
            gsonBuilder.setPrettyPrinting();
        }
        if(config.isCamelCase()) {
            gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
        } else {
            gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy
                    .LOWER_CASE_WITH_UNDERSCORES);
        }
        gson = gsonBuilder.create();
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
