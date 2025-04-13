package io.cdap.wrangler.utils;

public class TimeDuration {
    private final long millis;

    public TimeDuration(String timeStr) {
        timeStr = timeStr.trim().toLowerCase();
        if (timeStr.endsWith("ms")) {
            millis = Long.parseLong(timeStr.replace("ms", ""));
        } else if (timeStr.endsWith("s")) {
            millis = (long) (Double.parseDouble(timeStr.replace("s", "")) * 1000);
        } else if (timeStr.endsWith("m")) {
            millis = (long) (Double.parseDouble(timeStr.replace("m", "")) * 60 * 1000);
        } else {
            millis = Long.parseLong(timeStr);
        }
    }

    public long getMillis() {
        return millis;
    }
}

