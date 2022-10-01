package de.honoka.sdk.json.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import de.honoka.sdk.json.api.JsonArray;
import de.honoka.sdk.json.api.JsonObject;

import java.util.Set;

@SuppressWarnings("unchecked")
class FastJsonObject extends JsonObject {

	JSONObject originalJsonObject;

	//region init

	@Override
	protected void initEmpty() {
		originalJsonObject = new JSONObject();
	}

	@Override
	protected void initByJsonString(String jsonStr) {
		originalJsonObject = JSON.parseObject(jsonStr);
	}

	@Override
	protected <T> void initByOriginalJsonObject(T originalJsonObject) {
		this.originalJsonObject = (JSONObject) originalJsonObject;
	}

	FastJsonObject() {
		initEmpty();
	}

	FastJsonObject(String jsonStr) {
		initByJsonString(jsonStr);
	}

	FastJsonObject(JSONObject originalJsonObject) {
		initByOriginalJsonObject(originalJsonObject);
	}

	//endregion

	//region JsonObject

	@Override
	protected JsonObject getJsonObjectInThisObject(String key) {
		return new FastJsonObject(originalJsonObject.getJSONObject(key));
	}

	@Override
	protected <T> T getValueInThisObject(String key, Class<?> dataType) {
		return (T) new FastJsonDataConverter(originalJsonObject.get(key))
				.transfer(dataType);
	}

	@Override
	protected <T> JsonArray<T> getJsonArrayInThisObject(
			String key, Class<?> dataType) {
		return new FastJsonArray<>(originalJsonObject.getJSONArray(key), dataType);
	}

	@Override
	public <T> T toObject(Class<T> type) {
		//toJavaObject的feature参数暂未被使用，可以任意传递
		return originalJsonObject.toJavaObject(type,
				Common.parserConfig, 0);
	}

	@Override
	public String toString() {
		return originalJsonObject.toString(Common.serializerFeatures);
	}

	@Override
	public JsonObject add(String key, Object value) {
		originalJsonObject.put(key, Common.toOriginalJsonElement(value));
		return this;
	}

	@Override
	protected Set<Entry<String, Object>> originalEntrySet() {
		return originalJsonObject.entrySet();
	}

	@Override
	protected Object convertOriginalJsonData(Object data) {
		return new FastJsonDataConverter(data).transfer(null);
	}

	//endregion

	//region Map

	@Override
	public int size() {
		return originalJsonObject.size();
	}

	@Override
	public boolean containsKey(Object key) {
		return originalJsonObject.containsKey(key);
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
