package com.borunovv.jetpreter.interpreter;

import java.util.function.Consumer;

public class DevNull implements Consumer<String> {

    @Override
    public void accept(String s) {
    }
}
