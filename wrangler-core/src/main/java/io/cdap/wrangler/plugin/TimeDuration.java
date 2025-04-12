package io.cdap.wrangler.plugin;

public class TimeDuration {
    private long nanoseconds;

    public TimeDuration(String duration) {
        if (duration.endsWith("ms")) {
            this.nanoseconds = Long.parseLong(duration.replace("ms", "")) * 1_000_000;
        } else if (duration.endsWith("s")) {
            this.nanoseconds = Long.parseLong(duration.replace("s", "")) * 1_000_000_000;
        } else if (duration.endsWith("m")) {
            this.nanoseconds = Long.parseLong(duration.replace("m", "")) * 60 * 1_000_000_000;
        } else if (duration.endsWith("h")) {
            this.nanoseconds = Long.parseLong(duration.replace("h", "")) * 60 * 60 * 1_000_000_000;
        }
    }

    public long getNanoseconds() {
        return nanoseconds;
    }

    @Override
    public String toString() {
        return nanoseconds + " ns";
    }
}
