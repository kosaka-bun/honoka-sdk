package de.honoka.sdk.json.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import de.honoka.sdk.json.api.JsonArray;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Iterator;

@SuppressWarnings({ "unchecked" })
class FastJsonArray<T> extends JsonArray<T> {

    JSONArray originalJsonArray;

    //region init

    @Override
    protected void initEmpty() {
        originalJsonArray = new JSONArray();
    }

    @Override
    protected void initByJsonString(String jsonStr) {
        originalJsonArray = JSON.parseArray(jsonStr);
    }

    @Override
    protected <A> void initByOriginalJsonArray(A originalJsonArray) {
        this.originalJsonArray = (JSONArray) originalJsonArray;
    }

    FastJsonArray(Class<?> dataType) {
        super(dataType);
        initEmpty();
    }

    FastJsonArray(String jsonStr, Class<?> dataType) {
        super(dataType);
        initByJsonString(jsonStr);
    }

    FastJsonArray(JSONArray originalJsonArray, Class<?> dataType) {
        super(dataType);
        initByOriginalJsonArray(originalJsonArray);
    }

    //endregion

    @Override
    public T get(int index) {
        return (T) new FastJsonDataConverter(originalJsonArray.get(index))
                .transfer(dataType);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private final Iterator<Object> originalIterator =
                    originalJsonArray.iterator();

            @Override
            public boolean hasNext() {
                return originalIterator.hasNext();
            }

            @Override
            public T next() {
                FastJsonDataConverter fjdc = new FastJsonDataConverter(
                        originalIterator.next());
                if(fjdc.isJsonArray())
                    return (T) fjdc.toJsonArray(dataType);
                return (T) fjdc.transfer(dataType);
            }
        };
    }

    @Override
    public String toString() {
        return originalJsonArray.toString(Common.serializerFeatures);
    }

    @Override
    public String toPrettyString() {
        SerializerFeature[] serializerFeatures = ArrayUtils.add(
                Common.serializerFeatures,
                SerializerFeature.PrettyFormat
        );
        return originalJsonArray.toString(serializerFeatures);
    }

    @Override
    public int size() {
        return originalJsonArray.size();
    }

    @Override
    public boolean add(T t) {
        try {
            originalJsonArray.add(Common.toOriginalJsonElement(t));
            return true;
        } catch(Throwable th) {
            return false;
        }
    }

    @Override
    protected boolean originalRemove(int index) {
        Object elem = originalJsonArray.remove(index);
        return elem != null;
    }
}
