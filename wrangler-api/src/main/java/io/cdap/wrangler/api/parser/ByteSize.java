package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a byte size value, such as 500MB, 2GB, etc.
 */
public class ByteSize implements Token {
    private final String original;
    private final double value;
    private final String unit;

    /**
     * Constructs a ByteSize object from a string representation of a byte size.
     * 
     * @param value The byte size value as a string (e.g., "500MB", "2GB").
     */
    public ByteSize(String value) {
        this.original = value;
        Pattern pattern = Pattern.compile("([0-9.]+)([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            this.value = Double.parseDouble(matcher.group(1));
            this.unit = matcher.group(2).toUpperCase();
        } else {
            throw new IllegalArgumentException("Invalid byte size: " + value);
        }
    }

    /**
     * Converts the byte size to bytes based on the unit.
     * 
     * @return the value in bytes.
     */
    public long getBytes() {
        switch (unit) {
            case "B":
                return (long) value;
            case "KB":
                return (long) (value * 1000);
            case "MB":
                return (long) (value * 1_000_000);
            case "GB":
                return (long) (value * 1_000_000_000);
            // Add more units if necessary
            default:
                throw new IllegalArgumentException("Unknown byte unit: " + unit);
        }
    }

    /**
     * Returns the original string representation of the byte size.
     * 
     * @return the original byte size value.
     */
    @Override
    public String value() {
        return original;
    }

    /**
     * Returns the type of the token, which is BYTE_SIZE in this case.
     * 
     * @return the TokenType of this object.
     */
    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE; // Return the TokenType enum, not the name as a string
    }

    /**
     * Converts this ByteSize object to a JSON representation.
     * 
     * @return a JsonObject containing the type, value, and byte conversion.
     */
    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", type().name()); // Use name() to get the string value of the enum
        obj.addProperty("value", original);
        obj.addProperty("bytes", getBytes());
        return obj;
    }
}
