package de.honoka.sdk.json.api.util;

import de.honoka.sdk.json.api.JsonObject;

public class JsonMaker {

    /**
     * 按“键-值”的方式传递参数，组装任意Json
     */
    public static JsonObject arbitrary(Object... args) {
        JsonObject jo = JsonObject.of();
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
