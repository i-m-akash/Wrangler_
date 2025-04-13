package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a time duration value, such as 10s, 2h, etc.
 */
public class TimeDuration implements Token {
    private final String original;
    private final double numericValue;
    private final String unit;

    /**
     * Constructs a TimeDuration object from a string representation of a time duration.
     * 
     * @param value The time duration value as a string (e.g., "10s", "2h").
     */
    public TimeDuration(String value) {
        this.original = value;
        Pattern pattern = Pattern.compile("([0-9.]+)([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            this.numericValue = Double.parseDouble(matcher.group(1));
            this.unit = matcher.group(2).toLowerCase();
        } else {
            throw new IllegalArgumentException("Invalid time duration: " + value);
        }
    }

    /**
     * Converts the time duration to milliseconds based on the unit.
     * 
     * @return the value in milliseconds.
     */
    public long getMillis() {
        switch (unit) {
            case "ms":
                return (long) numericValue;
            case "s":
            case "sec":
                return (long) (numericValue * 1000);
            case "m":
            case "min":
                return (long) (numericValue * 60_000);
            case "h":
                return (long) (numericValue * 3_600_000);
            default:
                throw new IllegalArgumentException("Unknown time unit: " + unit);
        }
    }

    /**
     * Returns the original string representation of the time duration.
     * 
     * @return the original time duration value.
     */
    @Override
    public String value() {
        return original;
    }

    /**
     * Returns the type of the token, which is TIME_DURATION in this case.
     * 
     * @return the TokenType of this object.
     */
    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION; // Return the TokenType enum instead of the name as a String
    }

    /**
     * Converts this TimeDuration object to a JSON representation.
     * 
     * @return a JsonObject containing the type, value, and milliseconds conversion.
     */
    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", type().name()); // Use name() to get the string value of the enum
        obj.addProperty("value", original);
        obj.addProperty("millis", getMillis());
        return obj;
    }
}
