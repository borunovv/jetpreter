package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.util.SystemConstants;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PrintOutTest extends AbstractInterpreterServerTest {

    @Test
    public void print() {
        // ARRANGE
        String program = "" +
                "print \"Hello, \"\n" +
                "print \"World!\"";
        // ACT
        String output = interpretProgram(program);
        // VERIFY
        assertEquals(output, "Hello, World!");
    }

    @Test
    public void printWithEscaping() {
        // ARRANGE
        String program = "" +
                "print \"Hello,\\n\"\n" +
                "print \"\tWorld!\"";
        // ACT
        String output = interpretProgram(program);
        // VERIFY
        assertEquals(output,
                "Hello," + SystemConstants.LINE_SEPARATOR + "\tWorld!");
    }

    @Test
    public void out() {
        // ARRANGE
        String program = "out 1+2";
        // ACT
        String output = interpretProgram(program);
        // VERIFY
        assertEquals(output,
                "3" + SystemConstants.LINE_SEPARATOR);
    }
}