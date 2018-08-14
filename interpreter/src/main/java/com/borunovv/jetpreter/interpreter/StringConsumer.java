package com.borunovv.jetpreter.interpreter;

import java.io.StringWriter;
import java.util.function.Consumer;

/**
 * String consumer.
 * Used to collect program output into string.
 */
public class StringConsumer implements Consumer<String> {
    private final StringWriter writer = new StringWriter();

    @Override
    public void accept(String s) {
        writer.write(s);
    }

    @Override
    public String toString() {
        return writer.toString();
    }
}