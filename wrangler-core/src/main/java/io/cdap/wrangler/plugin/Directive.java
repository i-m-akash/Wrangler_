package io.cdap.wrangler.plugin;

import java.util.List;

public interface Directive {
    void execute(List<Row> rows);
}

