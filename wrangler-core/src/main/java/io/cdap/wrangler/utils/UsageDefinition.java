package io.cdap.wrangler.utils;

import io.cdap.wrangler.api.parser.TokenType;

import java.util.HashMap;
import java.util.Map;

public class UsageDefinition {
    private final String name;
    private final Map<String, TokenType> args;

    private UsageDefinition(String name, Map<String, TokenType> args) {
        this.name = name;
        this.args = args;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        private final String name;
        private final Map<String, TokenType> args = new HashMap<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder define(String arg, TokenType type) {
            args.put(arg, type);
            return this;
        }

        public UsageDefinition build() {
            return new UsageDefinition(name, args);
        }
    }
}
