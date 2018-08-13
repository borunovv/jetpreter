package com.borunovv.jetpreter.interpreter;

import org.junit.Test;

public class RangeTest extends AbstractInterpreterServerTest {

    @Test
    public void validRange() {
        // ARRANGE
        String program = "" +
                "var r = {1,10}\n" +
                "out r";
        // VERIFY
        assertProgramOutput(
                program,
                "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]");
    }

    @Test
    public void tooBigRange() {
        assertProgramOutput(
                "var r = {1,1000001}",
                "Error in line #1: Too big range: {1, 1000001}. Size must not exceed 1000000. Actual size: 1000001.");
    }

    @Test
    public void invalidRange() {
        assertProgramOutput(
                "var r = {10,1}",
                "Error in line #1: Bad range: {10, 1}. Left bound > Right bound.");
    }
}