package de.honoka.sdk.json.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.JsonObject;
import de.honoka.sdk.json.api.util.JsonConfig;

import java.util.Arrays;
import java.util.List;

//package-private
class Common {

    static SerializerFeature[] serializerFeatures;

    static final SerializeConfig serializeConfig = new SerializeConfig();

    static final ParserConfig parserConfig = new ParserConfig();

    static {
        init();
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    static void init() {
        JsonConfig config = JsonConfig.get();
        //serializerFeatures
        List<SerializerFeature> serializerFeatureList = Arrays.asList(
                SerializerFeature.WriteMapNullValue
        );
        if(config.isPretty()) {
            serializerFeatureList.add(SerializerFeature.PrettyFormat);
        }
        serializerFeatures = serializerFeatureList.toArray(new SerializerFeature[0]);
        //serializeConfig, parserConfig
        if(config.isCamelCase()) {
            serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.NoChange;
            parserConfig.propertyNamingStrategy = PropertyNamingStrategy.NoChange;
        } else {
            //序列化时转为下划线
            serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
            //提取时根据下划线提取
            parserConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        }
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
