package de.honoka.sdk.util.file.csv;

import de.honoka.sdk.util.code.ActionUtils;
import de.honoka.sdk.util.various.ReflectUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 将csv格式的表格加载为便于使用的对象
 */
public class CsvTable<T> implements Iterable<T> {

    @Getter
    private final List<Map<String, String>> rows = new ArrayList<>();

    private Class<T> dataType;

    public CsvTable(String csvText, Class<T> dataType) {
        setDataType(dataType);
        loadTable(csvText);
    }

    @SneakyThrows
    public CsvTable(File f, Class<T> dataType) {
        setDataType(dataType);
        loadTable(FileUtils.readFileToString(f, StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public CsvTable(InputStream is, Class<T> dataType) {
        setDataType(dataType);
        loadByInputStream(is);
    }

    @SneakyThrows
    public CsvTable(URL url, Class<T> dataType) {
        setDataType(dataType);
        try(InputStream is = url.openConnection().getInputStream()) {
            loadByInputStream(is);
        }
    }

    public void setDataType(Class<T> dataType) {
        if(dataType != null && Map.class.isAssignableFrom(dataType)) {
            throw new IllegalArgumentException("Please use DefaultCsvTable.");
        }
        this.dataType = dataType;
    }

    //该方法不关闭流
    @SneakyThrows
    private void loadByInputStream(InputStream is) {
        loadTable(new String(IOUtils.toByteArray(is), StandardCharsets.UTF_8));
    }

    private void loadTable(String csvText) {
        csvText = csvText.trim();
        String[] rows = csvText.split("\n");
        if(rows.length < 2) return;
        String[] headers = rows[0].trim().split(",");
        for(int i = 1; i < rows.length; i++) {
            String rowStr = rows[i].trim();
            String[] cols = rowStr.split(",");
            Map<String, String> row = new HashMap<>();
            for(int j = 0; j < headers.length; j++) {
                try {
                    row.put(headers[j].trim(), cols[j].trim());
                } catch(Throwable t) {
                    row.put(headers[j].trim(), null);
                }
            }
            this.rows.add(row);
        }
    }

    @SneakyThrows
    protected T rowToBean(Map<String, String> row) {
        T bean = ReflectUtils.newInstance(dataType);
        for(Field field : dataType.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            String value = row.get(fieldName);
            //表格中是空串，一律视为null值
            if(StringUtils.isEmpty(value)) {
                ActionUtils.doIgnoreException(() -> field.set(bean, null));
                continue;
            }
            switch(fieldType.getSimpleName()) {
                case "int":
                case "Integer":
                    field.set(bean, Integer.parseInt(value));
                    break;
                case "long":
                case "Long":
                    field.set(bean, Long.parseLong(value));
                    break;
                case "double":
                case "Double":
                    field.set(bean, Double.parseDouble(value));
                    break;
                case "boolean":
                case "Boolean":
                    field.set(bean, Boolean.parseBoolean(value));
                    break;
                case "String":
                    field.set(bean, value);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown field " +
                            "type in " + dataType.getName() + ": " + fieldName +
                            "(" + fieldType.getName() + ")");
            }
        }
        return bean;
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<Map<String, String>> realIterator = rows.iterator();
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return realIterator.hasNext();
            }

            @Override
            public T next() {
                return rowToBean(realIterator.next());
            }
        };
    }

    /**
     * 遍历表格，查找第一个符合条件的行（header列为value值的行），未找到返回null
     */
    public T find(String header, String value) {
        for(Map<String, String> row : rows) {
            if(row.get(header).equals(value)) return rowToBean(row);
        }
        return null;
    }

    public List<T> findAll(String header, String value) {
        List<T> list = new ArrayList<>();
        for(Map<String, String> row : rows) {
            if(row.get(header).equals(value)) list.add(rowToBean(row));
        }
        return list;
    }
}
