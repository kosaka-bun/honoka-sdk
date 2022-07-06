package de.honoka.util.json.gson;

import de.honoka.util.json.api.JsonArray;
import de.honoka.util.json.api.JsonDataConverter;
import de.honoka.util.json.api.JsonObject;

//package-private
class GsonJsonDataConverter extends
		JsonDataConverter<com.google.gson.JsonElement> {
	
	public GsonJsonDataConverter(com.google.gson.JsonElement data) {
		super(data);
	}
	
	@Override
	protected boolean isJsonObject() {
		return data.isJsonObject();
	}
	
	@Override
	protected boolean isJsonArray() {
		return data.isJsonArray();
	}
	
	@Override
	protected JsonObject toJsonObject() {
		return new GsonJsonObject(data.getAsJsonObject());
	}
	
	@Override
	protected <T1> JsonArray<T1> toJsonArray(Class<T1> type) {
		return new GsonJsonArray<>(data.getAsJsonArray(), type);
	}
	
	@Override
	protected Integer toInt() {
		return data.getAsInt();
	}
	
	@Override
	protected Long toLong() {
		return data.getAsLong();
	}
	
	@Override
	protected Double toDouble() {
		return data.getAsDouble();
	}
	
	@Override
	protected Boolean toBoolean() {
		return data.getAsBoolean();
	}
	
	@Override
	protected String castToString() {
		return data.getAsString();
	}
	
	@Override
	protected Byte toByte() {
		return data.getAsByte();
	}
	
	@Override
	protected Character toChar() {
		return data.getAsString().charAt(0);
	}
	
	@Override
	protected <T1> T1 toObject(Class<T1> type) {
		return Common.gson.fromJson(data, type);
	}
	
	@Override
	protected Object toUnknownType() {
		if(data.isJsonObject()) return toJsonObject();
		if(data.isJsonArray()) return toJsonArray();
		if(data.isJsonPrimitive()) {
			com.google.gson.JsonPrimitive ojp = data.getAsJsonPrimitive();
			if(ojp.isString()) return ojp.getAsString();
			if(ojp.isBoolean()) return ojp.getAsBoolean();
			if(ojp.isNumber()) return ojp.getAsNumber();
		}
		if(data.isJsonNull()) return null;
		throw new IllegalArgumentException("Unknown data type");
	}
}
