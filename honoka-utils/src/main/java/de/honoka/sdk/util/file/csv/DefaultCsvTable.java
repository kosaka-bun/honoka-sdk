package de.honoka.sdk.util.file.csv;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

public class DefaultCsvTable extends CsvTable<Map<String, String>> {

    public DefaultCsvTable(String csvText) {
        super(csvText, null);
    }

    @Override
    protected Map<String, String> rowToBean(Map<String, String> row) {
        return row;
    }

    @Override
    public @NotNull Iterator<Map<String, String>> iterator() {
        return getRows().iterator();
    }
}
