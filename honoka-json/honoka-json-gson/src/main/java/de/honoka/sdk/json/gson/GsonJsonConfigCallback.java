package de.honoka.sdk.json.gson;

import com.google.gson.FieldNamingPolicy;
import de.honoka.sdk.json.api.service.JsonConfigCallback;
import de.honoka.sdk.util.various.ReflectUtils;

public class GsonJsonConfigCallback implements JsonConfigCallback {

    @Override
    public void onCamelCaseSet(boolean camelCase) {
        FieldNamingPolicy policy = camelCase ?
                FieldNamingPolicy.IDENTITY :
                FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
        Common.gsonBuilder.setFieldNamingPolicy(policy);
        Common.buildGson();
    }

    @Override
    public void onPrettySet(boolean pretty) {
        if(pretty) {
            Common.gsonBuilder.setPrettyPrinting();
        } else {
            ReflectUtils.setFieldValue(
                    Common.gsonBuilder,
                    "prettyPrinting",
                    false
            );
        }
        Common.buildGson();
    }
}
