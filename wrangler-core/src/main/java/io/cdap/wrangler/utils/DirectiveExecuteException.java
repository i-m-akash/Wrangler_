package io.cdap.wrangler.utils;

public class DirectiveExecuteException extends Exception {
    public DirectiveExecuteException(String message) {
        super(message);
    }

    public DirectiveExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
