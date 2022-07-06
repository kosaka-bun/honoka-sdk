package de.honoka.util.json.gson;

import de.honoka.util.json.api.JsonMaker;
import de.honoka.util.json.api.JsonObject;

public class GsonJsonMaker extends JsonMaker {
	
	@Override
	protected JsonObject newJsonObject() {
		return new GsonJsonObject();
	}
}
