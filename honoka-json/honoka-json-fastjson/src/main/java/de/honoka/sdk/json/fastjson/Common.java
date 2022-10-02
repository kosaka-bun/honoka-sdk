package de.honoka.sdk.json.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.JsonObject;
import de.honoka.sdk.json.api.service.JsonConfigCallback;
import de.honoka.sdk.json.api.util.JsonConfig;

//package-private
class Common {

    static SerializerFeature[] serializerFeatures;

    static final SerializeConfig serializeConfig = new SerializeConfig();

    static final ParserConfig parserConfig = new ParserConfig();

    static {
        init();
    }

    static void init() {
        JsonConfig config = JsonConfig.get();
        JsonConfigCallback callback = JsonConfigCallback.get();
        //serializerFeatures
        callback.onPrettySet(config.isPretty());
        //serializeConfig, parserConfig
        callback.onCamelCaseSet(config.isCamelCase());
    }

    static Object toOriginalJsonElement(Object value) {
        //本框架的Json数据
        if(value instanceof FastJsonObject)
            return ((FastJsonObject) value).originalJsonObject;
        if(value instanceof FastJsonArray)
            return ((FastJsonArray<?>) value).originalJsonArray;
        //其他框架的Json数据
        if(value instanceof JsonObject || value instanceof JsonArray)
            return JSON.parse(value.toString());
        //其他类型
        return JSON.parse(JSON.toJSONString(value, serializeConfig,
                serializerFeatures));
    }
}
