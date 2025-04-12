package io.cdap.wrangler.plugin;
import java.util.ArrayList;
import java.util.List;
import io.cdap.wrangler.plugin.Row;
import io.cdap.wrangler.plugin.ByteSize;
import io.cdap.wrangler.plugin.TimeDuration;
import io.cdap.wrangler.plugin.AggregateStatsDirective;

public class Main {

    public static void main(String[] args) {
        // Create sample rows
        List<Row> rows = new ArrayList<>();
        rows.add(createSampleRow("100KB", "10s"));
        rows.add(createSampleRow("200KB", "20s"));
        rows.add(createSampleRow("150KB", "15s"));

        // Create an AggregateStatsDirective instance
        AggregateStatsDirective directive = new AggregateStatsDirective(
            "byteSize", "timeDuration", "totalByteSize", "totalTimeDuration"
        );

        // Execute the directive
        directive.execute(rows);

        // Print the rows to see the result
        for (Row row : rows) {
            System.out.println(row);
        }
    }

    private static Row createSampleRow(String byteSize, String timeDuration) {
        Row row = new Row();
        row.setValue("byteSize", new ByteSize(byteSize));
        row.setValue("timeDuration", new TimeDuration(timeDuration));
        return row;
    }
}
