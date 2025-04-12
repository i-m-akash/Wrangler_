package io.cdap.wrangler.plugin;

public class ByteSize {
    private long bytes;

    public ByteSize(String size) {
        if (size.endsWith("KB")) {
            this.bytes = Long.parseLong(size.replace("KB", "")) * 1024;
        } else if (size.endsWith("MB")) {
            this.bytes = Long.parseLong(size.replace("MB", "")) * 1024 * 1024;
        } else if (size.endsWith("GB")) {
            this.bytes = Long.parseLong(size.replace("GB", "")) * 1024 * 1024 * 1024;
        } else if (size.endsWith("TB")) {
            this.bytes = Long.parseLong(size.replace("TB", "")) * 1024 * 1024 * 1024 * 1024;
        }
    }

    public long getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return bytes + " bytes";
    }
}
