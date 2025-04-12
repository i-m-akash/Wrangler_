package io.cdap.wrangler.plugin;

import java.util.List;

public class AggregateStatsDirective {
    private final String byteSizeCol;
    private final String timeDurationCol;
    private final String totalByteSizeCol;
    private final String totalTimeDurationCol;

    public AggregateStatsDirective(String byteSizeCol, String timeDurationCol,
                                   String totalByteSizeCol, String totalTimeDurationCol) {
        this.byteSizeCol = byteSizeCol;
        this.timeDurationCol = timeDurationCol;
        this.totalByteSizeCol = totalByteSizeCol;
        this.totalTimeDurationCol = totalTimeDurationCol;
    }

    public void execute(List<Row> rows) {
        long totalBytes = 0;
        long totalDuration = 0;

        for (Row row : rows) {
            ByteSize size = (ByteSize) row.getValue(byteSizeCol);
            TimeDuration duration = (TimeDuration) row.getValue(timeDurationCol);
            if (size != null) totalBytes += size.getBytes();
            if (duration != null) totalDuration += duration.getNanoseconds();
        }

        Row summaryRow = new Row();
        summaryRow.setValue(totalByteSizeCol, new ByteSize(String.valueOf(totalBytes)));
        summaryRow.setValue(totalTimeDurationCol, new TimeDuration(String.valueOf(totalDuration)));

        rows.add(summaryRow);
    }
}
