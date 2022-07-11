package de.honoka.sdk.json.fastjson;

import de.honoka.sdk.json.api.JsonMaker;
import de.honoka.sdk.json.api.JsonObject;

public class FastJsonMaker extends JsonMaker {
	
	@Override
	protected JsonObject newJsonObject() {
		return new FastJsonObject();
	}
}
