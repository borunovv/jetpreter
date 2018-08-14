package com.borunovv.jetpreter.interpreter;

import org.junit.Test;

public class ReservedKeywordsTest extends AbstractInterpreterServerTest {

    @Test
    public void varMapReduce() {
        assertProgramOutput("var Var=1", "");
        assertProgramOutput("var VAr=1", "");
        assertProgramOutput("var VAR=1", "");
        assertProgramOutputHasError("var var = 1", "Error in line #1: Encountered \" \"var\"");
        assertProgramOutputHasError("out map({1,2}, var -> var + 1)", "line #1: Encountered \" \"var\"");

        // map as variable name is legal
        assertProgramOutput("var map=1", "");
        assertProgramOutput("out map({1,2}, map -> map + 1)", "[2, 3]");


        // same for reduce
        assertProgramOutput("var reduce=1", "");
        assertProgramOutput("out reduce({1,2}, 0, map reduce -> map + reduce)", "3");
    }
}