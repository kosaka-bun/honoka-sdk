package de.honoka.util.json.api;

public abstract class JsonMaker {
	
	protected abstract JsonObject newJsonObject();
	
	/**
	 * 按“键-值”的方式传递参数，组装任意Json
	 */
	public JsonObject arbitrary(Object... args) {
		JsonObject jo = newJsonObject();
		for(int i = 0; i < args.length; i++) {
			String key = args[i].toString();
			if(i + 1 < args.length) {
				i++;
				jo.add(key, args[i]);
			} else {
				jo.add(key, null);
			}
		}
		return jo;
	}
}
