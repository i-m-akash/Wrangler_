package io.cdap.wrangler.utils;

public class ByteSize {
    private final long bytes;

    public ByteSize(String sizeStr) {
        sizeStr = sizeStr.trim().toUpperCase();
        if (sizeStr.endsWith("MB")) {
            bytes = (long) (Double.parseDouble(sizeStr.replace("MB", "")) * 1024 * 1024);
        } else if (sizeStr.endsWith("KB")) {
            bytes = (long) (Double.parseDouble(sizeStr.replace("KB", "")) * 1024);
        } else if (sizeStr.endsWith("GB")) {
            bytes = (long) (Double.parseDouble(sizeStr.replace("GB", "")) * 1024 * 1024 * 1024);
        } else {
            bytes = Long.parseLong(sizeStr);
        }
    }

    public long getBytes() {
        return bytes;
    }
}

