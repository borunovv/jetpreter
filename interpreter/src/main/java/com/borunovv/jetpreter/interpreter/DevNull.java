package com.borunovv.jetpreter.interpreter;

import java.util.function.Consumer;

/**
 * Fake program output for test purposes. Does nothing.
 */
public class DevNull implements Consumer<String> {

    @Override
    public void accept(String s) {
    }
}
