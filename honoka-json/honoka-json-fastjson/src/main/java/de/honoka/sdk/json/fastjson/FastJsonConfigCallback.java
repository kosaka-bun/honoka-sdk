package de.honoka.sdk.json.fastjson;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializerFeature;
import de.honoka.sdk.json.api.service.JsonConfigCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastJsonConfigCallback implements JsonConfigCallback {

    @Override
    public void onCamelCaseSet(boolean camelCase) {
        PropertyNamingStrategy strategy = camelCase ?
                PropertyNamingStrategy.NoChange :
                PropertyNamingStrategy.SnakeCase;
        Common.serializeConfig.propertyNamingStrategy = strategy;
        Common.parserConfig.propertyNamingStrategy = strategy;
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    @Override
    public void onPrettySet(boolean pretty) {
        List<SerializerFeature> serializerFeatureList = Arrays.asList(
                SerializerFeature.WriteMapNullValue
        );
        serializerFeatureList = new ArrayList<>(serializerFeatureList);
        if(pretty) {
            serializerFeatureList.add(SerializerFeature.PrettyFormat);
        }
        Common.serializerFeatures = serializerFeatureList
                .toArray(new SerializerFeature[0]);
    }
}
