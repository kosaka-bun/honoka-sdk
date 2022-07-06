package de.honoka.util.json.fastjson;

import de.honoka.util.json.api.JsonMaker;
import de.honoka.util.json.api.JsonObject;

public class FastJsonMaker extends JsonMaker {
	
	@Override
	protected JsonObject newJsonObject() {
		return new FastJsonObject();
	}
}
