package de.honoka.sdk.json.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.JsonObject;

import java.util.Set;

@SuppressWarnings({ "unchecked", "rawtypes" })
class GsonJsonObject extends JsonObject {

    //package-private
    com.google.gson.JsonObject originalJsonObject;

    //region init

    @Override
    protected void initEmpty() {
        originalJsonObject = new com.google.gson.JsonObject();
    }

    @Override
    protected void initByJsonString(String jsonStr) {
        originalJsonObject = com.google.gson.JsonParser.parseString(jsonStr)
                .getAsJsonObject();
    }

    @Override
    protected <T> void initByOriginalJsonObject(T originalJsonObject) {
        this.originalJsonObject = (com.google.gson.JsonObject) originalJsonObject;
    }

    GsonJsonObject() {
        initEmpty();
    }

    GsonJsonObject(String jsonStr) {
        initByJsonString(jsonStr);
    }

    GsonJsonObject(com.google.gson.JsonObject originalJsonObject) {
        initByOriginalJsonObject(originalJsonObject);
    }

    //endregion

    //region JsonObject

    @Override
    protected JsonObject getJsonObjectInThisObject(String key) {
        return new GsonJsonObject(originalJsonObject.getAsJsonObject(key));
    }

    @Override
    protected <T> T getValueInThisObject(String key, Class<?> dataType) {
        return (T) new GsonJsonDataConverter(originalJsonObject.get(key))
                .transfer(dataType);
    }

    @Override
    protected <T> JsonArray<T> getJsonArrayInThisObject(
            String key, Class<?> dataType) {
        return new GsonJsonArray<>(originalJsonObject.getAsJsonArray(key), dataType);
    }

    @Override
    public <T> T toObject(Class<T> type) {
        return Common.gson.fromJson(originalJsonObject, type);
    }

    @Override
    public String toString() {
        return Common.gson.toJson(originalJsonObject);
    }

    @Override
    public String toPrettyString() {
        Gson gson = Common.copyBuilder().setPrettyPrinting().create();
        return gson.toJson(originalJsonObject);
    }

    @Override
    public JsonObject add(String key, Object value) {
        originalJsonObject.add(key, Common.toOriginalJsonElement(value));
        return this;
    }

    @Override
    protected Set<Entry<String, Object>> originalEntrySet() {
        return (Set) originalJsonObject.entrySet();
    }

    @Override
    protected Object convertOriginalJsonData(Object data) {
        return new GsonJsonDataConverter((JsonElement) data).transfer(null);
    }

    //endregion

    //region Map

    @Override
    public int size() {
        return originalJsonObject.size();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean containsKey(Object key) {
        return originalJsonObject.keySet().contains(key);
    }

    @Override
    public Object remove(Object key) {
        return originalJsonObject.remove((String) key);
    }

    @Override
    public Set<String> keySet() {
        return originalJsonObject.keySet();
    }

    //endregion
}
