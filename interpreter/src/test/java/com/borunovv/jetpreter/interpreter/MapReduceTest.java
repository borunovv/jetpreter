package com.borunovv.jetpreter.interpreter;

import org.junit.Test;

public class MapReduceTest extends AbstractInterpreterServerTest {

    @Test
    public void simpleMap() {
        // ARRANGE
        String program = "" +
                "var list = map({1,10}, a -> a * 2)\n" +
                "out list";
        // VERIFY
        assertProgramOutput(
                program,
                "[2, 4, 6, 8, 10, 12, 14, 16, 18, 20]");
    }

    @Test
    public void nestedMaps() {
        // ARRANGE
        String program = "" +
                "var list = map(map(map({1,10}, a -> a * 2), a -> a * 3), a -> a * 5)\n" +
                "out list";
        // VERIFY
        assertProgramOutput(
                program,
                "[30, 60, 90, 120, 150, 180, 210, 240, 270, 300]");
    }

    @Test
    public void simpleReduce() {
        // ARRANGE
        String program = "" +
                "var sum = reduce({1,100}, 0, accum next_item -> accum + next_item)\n" +
                "out sum";
        // VERIFY
        assertProgramOutput(program, "5050");
    }

    @Test
    public void nestedMapInReduce() {
        // ARRANGE
        String program = "" +
                "var result = reduce(map(map(map({1,10}, a -> a * 2), a -> a * 3), a -> a * 5), 0, accum next -> accum + next ^ 2)\n" +
                "out result";
        // VERIFY
        assertProgramOutput(program, "346500.0");
    }

    @Test
    public void calcPI() {
        // ARRANGE
        String program = "" +
                "var n = 500\n" +
                "var sequence = map({0, n}, i -> (-1)^i / (2 * i + 1))\n" +
                "var pi = 4 * reduce(sequence, 0, x y -> x + y)\n" +
                "print \"pi = \"\n" +
                "out pi\n";

        // VERIFY
        assertProgramOutput(program, "pi = 3.143588659586");
    }

    @Test
    public void buggyLambda() {
        assertProgramOutputHasError(
                "out map({0, 10},  -> i + 1)",
                "Error in line #1: Encountered \" \"->\"");

        assertProgramOutputHasError(
                "out map({0, 10}, a -> i + 1)",
                "Error in line #1: Undefined variable: 'i'. Forgot to declare ?");

        assertProgramOutputHasError(
                "out map({0, 10}, a b -> a + 1)",
                "Error in line #1: Function 'map': 2nd argument lambda must have exactly 1 argument.");

        assertProgramOutputHasError(
                "out reduce({0, 10}, 0, a -> a + 1)",
                "Error in line #1: Function 'reduce': 3nd argument lambda must have exactly 2 arguments.");

        // We do not support lambdas with more than 2 arguments
        assertProgramOutputHasError(
                "out reduce({0, 10}, 0, a b c -> a + 1)", // 3 arguments in lambda
                "Error in line #1: Encountered \" <IDENTIFIER>");

        assertProgramOutputHasError(
                "out reduce({0, 10}, 0, a b -> b/0)",
                "Error in line #1: Division by zero.");
    }
}