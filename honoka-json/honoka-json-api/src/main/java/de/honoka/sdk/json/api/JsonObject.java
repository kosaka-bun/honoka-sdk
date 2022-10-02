package de.honoka.sdk.json.api;

import de.honoka.sdk.json.api.service.JsonObjectService;
import de.honoka.sdk.util.text.TextUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.NotImplementedException;

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class JsonObject implements Map<String, Object> {

    protected abstract JsonObject getJsonObjectInThisObject(String key);

    protected abstract <T> T getValueInThisObject(String key, Class<?> dataType);

    protected abstract <T> JsonArray<T> getJsonArrayInThisObject(
            String key, Class<?> dataType);

    //region 用于提醒实现构造器的方法

    protected abstract void initEmpty();

    protected abstract void initByJsonString(String jsonStr);

    protected abstract <T> void initByOriginalJsonObject(T originalJsonObject);

    //endregion

    private String[] splitJsonObjectPathAndKey(String path) {
        String joPath, key;
        if(path.contains(".")) {
            joPath = path.substring(0, path.lastIndexOf("."));
            key = path.substring(path.lastIndexOf(".") + 1);
        } else {
            joPath = null;
            key = path;
        }
        return new String[] { joPath, key };
    }

    public JsonObject getJsonObject(String path) {
        String[] part = path.split("\\.");
        JsonObject jo = this;
        for(String s : part) {
            jo = jo.getJsonObjectInThisObject(s);
        }
        return jo;
    }

    private <T> T getValue(String path, Class<?> dataType) {
        String[] jsonPathAndKey = splitJsonObjectPathAndKey(path);
        if(jsonPathAndKey[0] != null) {
            return getJsonObject(jsonPathAndKey[0])
                    .getValueInThisObject(jsonPathAndKey[1], dataType);
        } else {
            return getValueInThisObject(jsonPathAndKey[1], dataType);
        }
    }

    public String getString(String path) {
        return getValue(path, String.class);
    }

    public int getInt(String path) {
        return getValue(path, Integer.class);
    }

    public long getLong(String path) {
        return getValue(path, Long.class);
    }

    public double getDouble(String path) {
        return getValue(path, Double.class);
    }

    public boolean getBoolean(String path) {
        return getValue(path, Boolean.class);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    @SneakyThrows
    public Date getDate(String path) {
        return TextUtils.getSimpleDateFormat().parse(getString(path));
    }

    @SneakyThrows
    public Date getDate(String path, String pattern) {
        return new SimpleDateFormat(pattern).parse(getString(path));
    }

    public <T> JsonArray<T> getJsonArray(String path, Class<?> dataType) {
        String[] jsonPathAndKey = splitJsonObjectPathAndKey(path);
        if(jsonPathAndKey[0] != null) {
            return getJsonObject(jsonPathAndKey[0])
                    .getJsonArrayInThisObject(jsonPathAndKey[1], dataType);
        } else {
            return getJsonArrayInThisObject(jsonPathAndKey[1], dataType);
        }
    }

    protected abstract Set<Entry<String, Object>> originalEntrySet();

    /**
     * 将框架自己实现的Json数据的类（如JsonObject、JsonArray等）转化为
     * 基本数据类型或符合API标准的JsonObject、JsonArray
     */
    protected abstract Object convertOriginalJsonData(Object data);

    public abstract String toString();

    public abstract String toPrettyString();

    public abstract JsonObject add(String key, Object value);

    public abstract <T> T toObject(Class<T> type);

    //region Map

    //entry的value只能是基本数据类型以及JsonObject、JsonArray和null
    @Override
    public Set<Entry<String, Object>> entrySet() {
        Map<String, Object> map = new HashMap<>();
        for(Entry<String, Object> entry : originalEntrySet()) {
            map.put(entry.getKey(), convertOriginalJsonData(entry.getValue()));
        }
        return map.entrySet();
    }

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }

    @Deprecated
    @Override
    public Object get(Object key) {
        return getValueInThisObject((String) key, null);
    }

    @Deprecated
    @Override
    public Object put(String key, Object value) {
        add(key, value);
        return null;	//返回值表示前一个该key在put前所存储的值
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        for(Entry<? extends String, ?> entry : m.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        initEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        for(Entry<String, Object> entry : entrySet()) {
            if(entry.getValue().equals(value)) return true;
        }
        return false;
    }

    @Override
    public Collection<Object> values() {
        List<Object> values = new ArrayList<>();
        for(Entry<String, Object> entry : entrySet()) {
            values.add(entry.getValue());
        }
        return values;
    }

    //endregion

    public static JsonObject of(String jsonStr) {
        ServiceLoader<JsonObjectService> loader = ServiceLoader.load(
                JsonObjectService.class);
        for(JsonObjectService service : loader) {
            if(jsonStr == null) {
                return service.of();
            }
            return service.of(jsonStr);
        }
        throw new NotImplementedException(JsonObjectService.class.getName());
    }

    public static JsonObject of() {
        return of(null);
    }
}
