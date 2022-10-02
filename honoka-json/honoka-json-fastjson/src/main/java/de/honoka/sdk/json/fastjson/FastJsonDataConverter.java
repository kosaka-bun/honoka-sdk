package de.honoka.sdk.json.fastjson;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.JsonObject;
import de.honoka.sdk.json.api.util.JsonDataConverter;

//package-private
class FastJsonDataConverter extends JsonDataConverter<Object> {

    public FastJsonDataConverter(Object data) {
        super(data);
    }

    @Override
    protected boolean isJsonObject() {
        return data instanceof JSONObject;
    }

    @Override
    protected boolean isJsonArray() {
        return data instanceof JSONArray;
    }

    @Override
    protected JsonObject toJsonObject() {
        return new FastJsonObject((JSONObject) data);
    }

    @Override
    protected <T1> JsonArray<T1> toJsonArray(Class<T1> type) {
        return new FastJsonArray<>((JSONArray) data, type);
    }

    @Override
    protected Integer toInt() {
        return TypeUtils.castToInt(data);
    }

    @Override
    protected Long toLong() {
        return TypeUtils.castToLong(data);
    }

    @Override
    protected Double toDouble() {
        return TypeUtils.castToDouble(data);
    }

    @Override
    protected Boolean toBoolean() {
        return data == null ? null : TypeUtils.castToBoolean(data);
    }

    @Override
    protected String castToString() {
        return data == null ? null : data.toString();
    }

    @Override
    protected Byte toByte() {
        return TypeUtils.castToByte(data);
    }

    @Override
    protected Character toChar() {
        return castToString().charAt(0);
    }

    @Override
    protected <T1> T1 toObject(Class<T1> type) {
        return TypeUtils.cast(data, type, Common.parserConfig);
    }

    @Override
    protected Object toUnknownType() {
        if(isJsonObject()) return toJsonObject();
        if(isJsonArray()) return toJsonArray();
        return data;
    }
}
