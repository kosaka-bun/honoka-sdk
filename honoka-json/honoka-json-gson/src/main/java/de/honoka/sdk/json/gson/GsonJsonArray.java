package de.honoka.sdk.json.gson;

import de.honoka.sdk.json.api.JsonArray;

import java.util.Iterator;

@SuppressWarnings({ "unchecked" })
class GsonJsonArray<T> extends JsonArray<T> {

    com.google.gson.JsonArray originalJsonArray;

    //region init

    @Override
    protected void initEmpty() {
        originalJsonArray = new com.google.gson.JsonArray();
    }

    @Override
    protected void initByJsonString(String jsonStr) {
        originalJsonArray = com.google.gson.JsonParser.parseString(jsonStr)
                .getAsJsonArray();
    }

    @Override
    protected <A> void initByOriginalJsonArray(A originalJsonArray) {
        this.originalJsonArray = (com.google.gson.JsonArray) originalJsonArray;
    }

    GsonJsonArray(Class<?> dataType) {
        super(dataType);
        initEmpty();
    }

    GsonJsonArray(String jsonStr, Class<?> dataType) {
        super(dataType);
        initByJsonString(jsonStr);
    }

    GsonJsonArray(com.google.gson.JsonArray originalJsonArray, Class<?> dataType) {
        super(dataType);
        initByOriginalJsonArray(originalJsonArray);
    }

    //endregion

    @Override
    public T get(int index) {
        return (T) new GsonJsonDataConverter(originalJsonArray.get(index))
                .transfer(dataType);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private final Iterator<com.google.gson.JsonElement>
                    originalIterator = originalJsonArray.iterator();

            @Override
            public boolean hasNext() {
                return originalIterator.hasNext();
            }

            @Override
            public T next() {
                GsonJsonDataConverter gjdc = new GsonJsonDataConverter(
                        originalIterator.next());
                if(gjdc.isJsonArray())
                    return (T) gjdc.toJsonArray(dataType);
                return (T) gjdc.transfer(dataType);
            }
        };
    }

    @Override
    public String toString() {
        return originalJsonArray.toString();
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
