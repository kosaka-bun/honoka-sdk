package de.honoka.util.json.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.honoka.util.json.api.JsonArray;
import de.honoka.util.json.api.JsonObject;

//package-private
class Common {
	
	static final Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			//驼峰转下划线
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.serializeNulls()
			.create();
	
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
		return com.google.gson.JsonParser.parseString(Common.gson.toJson(value));
	}
}
