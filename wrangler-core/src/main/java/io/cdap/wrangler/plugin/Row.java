package io.cdap.wrangler.plugin;

import java.util.HashMap;
import java.util.Map;

public class Row {
    private Map<String, Object> values = new HashMap<>();

    // Get the value for a column
    public Object getValue(String columnName) {
        return values.get(columnName);
    }

    // Set the value for a column
    public void setValue(String columnName, Object value) {
        values.put(columnName, value);
    }

    @Override
    public String toString() {
        return "Row{" + "values=" + values + '}';
    }
}
