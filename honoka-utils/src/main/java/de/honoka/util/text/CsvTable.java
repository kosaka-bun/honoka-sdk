package de.honoka.util.text;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将csv格式的表格加载为便于使用的对象
 */
public class CsvTable {

    public final List<Map<String, String>> rows = new ArrayList<>();

    @SneakyThrows
    public CsvTable(File f) {
        loadTable(FileUtils.readFileToString(f, StandardCharsets.UTF_8));
    }

    public CsvTable(String filePath) {
        this(new File(filePath));
    }

    private void loadTable(String csvText) {
        csvText = csvText.trim();
        String[] rows = csvText.split("\n");
        if(rows.length < 2) return;
        String[] headers = rows[0].split(",");
        for(int i = 1; i < rows.length; i++) {
            String[] cols = rows[i].split(",");
            Map<String, String> row = new HashMap<>();
            for(int j = 0; j < headers.length; j++) {
                try {
                    row.put(headers[j].trim(), cols[j].trim());
                } catch(Exception e) {
                    row.put(headers[j].trim(), null);
                }
            }
            this.rows.add(row);
        }
    }

    /**
     * 遍历表格，查找第一个符合条件的行（header列为value值的行），未找到返回null
     */
    public Map<String, String> search(String header, String value) {
        for(Map<String, String> row : rows) {
            if(row.get(header).equals(value)) return row;
        }
        return null;
    }
}
