package com.borunovv.jetpreter.interpreter;

import org.junit.Test;

public class MapReduceTest extends AbstractInterpreterServerTest {

    @Test
    public void validMapping() {
        // ARRANGE
        String program = "" +
                "var list = map({1,10}, a -> a * 2)\n" +
                "out list";
        // VERIFY
        assertProgramOutput(
                program,
                "[2, 4, 6, 8, 10, 12, 14, 16, 18, 20]");
    }
}