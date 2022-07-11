package de.honoka.sdk.json.gson;

import de.honoka.sdk.json.api.JsonMaker;
import de.honoka.sdk.json.api.JsonObject;

public class GsonJsonMaker extends JsonMaker {
	
	@Override
	protected JsonObject newJsonObject() {
		return new GsonJsonObject();
	}
}
