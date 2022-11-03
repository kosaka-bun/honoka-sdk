package de.honoka.sdk.json.gson;

import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

public class AllTest {

    @Test
    public void test1() {
        System.out.println(Common.gson.fromJson(new JsonPrimitive("test"),
                String.class));
    }
}
