package com.borunovv.jetpreter.interpreter;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ArithmeticsTest extends AbstractInterpreterServerTest {

    @Test
    public void numbers() {
        assertProgramOutput("out 0", "0");
        assertProgramOutput("out 0.0", "0.0");
        assertProgramOutput("out .0", "0.0");
        assertProgramOutput("out -0", "0");
        assertProgramOutput("out -0.0", "0.0");
        assertProgramOutput("out -.0", "0.0");

        assertProgramOutput("out 00", "0");
        assertProgramOutput("out 0000", "0");
        assertProgramOutput("out 0001", "1");
        assertProgramOutput("out 00.00", "0.0");
        assertProgramOutput("out 00.", "0.0");

        assertProgramOutput("out 1", "1");
        assertProgramOutput("out .123", "0.123");
        assertProgramOutput("out 1.234", "1.234");
        assertProgramOutput("out -1", "-1");
        assertProgramOutput("out -.123", "-0.123");
        assertProgramOutput("out -1.234", "-1.234");

        assertProgramOutputHasError("out --1", "Error in line #1: Encountered \" \"-\" \"");
        assertProgramOutputHasError("out 1a", "Error in line #1: Encountered \" <IDENTIFIER> \"a \"");
        assertProgramOutputHasError("out 1..1", "Error in line #1: Encountered \" <FLOAT> \".1 \"");
    }

    @Test
    public void addAndSubstract() {
        assertProgramOutput("out 1+2-3", "0");
        assertProgramOutput("out 1 + 2 - 3", "0");
        assertProgramOutput("out 1 +2- 3", "0");
        assertProgramOutput("out 1+2-3-4", "-4");
        assertProgramOutput("out 1+2-(3-4)", "4");
        assertProgramOutput("out 1-(2-(3-4))", "-2");
        assertProgramOutput("out 1-2-3-4+1+2+3+4", "2");
        assertProgramOutput("out .1-.2-.3-.4+.1+.2+.3+.4", "0.2");
        assertProgramOutput("out 0.1-0.2-0.3-0.4+0.1+0.2+0.3+0.4", "0.2");

        assertProgramOutput("out 1+2--3", "6");
        assertProgramOutput("out -1--2--3", "4");
        assertProgramOutput("out -1 - -2 - -3", "4");
        assertProgramOutput("out -0.1 --.2 - -0.3", "0.4");

        assertProgramOutputHasError("out 1++1", "Error in line #1: Encountered \" \"+\" \"");
    }

    @Test
    public void multiplyAndDivision() {
        assertProgramOutput("out 2*6/3", "4");
        assertProgramOutput("out 2 *6/ 3", "4");
        assertProgramOutput("out 2 * 6    / 3", "4");
        assertProgramOutput("out 1/2", "0.5");
        assertProgramOutput("out 2.0/1.0", "2.0");
        assertProgramOutput("out 0/1", "0");
        assertProgramOutput("out 0/1.0", "0.0");
        assertProgramOutput("out 1*2*3*4*5", "120");
        assertProgramOutput("out 1*2*3*4*5/1/2/3/4/5", "1");
        assertProgramOutput("out 1*2*3*4*5/1/2/3/4/5.", "1.0");
        assertProgramOutput("out 1*2*3*4*5/1/2/3/4/5.", "1.0");

        assertProgramOutput("out 1/(1/2)", "2.0");
        assertProgramOutput("out 1/(1/(2/4))", "0.5");
        assertProgramOutput("out 100/(10/(4/2))", "20");

        assertProgramOutput("out 1/0", "Error in line #1: Division by zero.");
        assertProgramOutput("out 1/0.", "Error in line #1: Division by zero.");
        assertProgramOutput("out 1/0.0", "Error in line #1: Division by zero.");
        assertProgramOutput("out 0/0", "Error in line #1: Division by zero.");
        assertProgramOutput("out 0/0.0", "Error in line #1: Division by zero.");
        assertProgramOutput("out 0.0/0", "Error in line #1: Division by zero.");
        assertProgramOutput("out 0.0/0.0", "Error in line #1: Division by zero.");
        assertProgramOutput("out .0/.0", "Error in line #1: Division by zero.");
        assertProgramOutput("out (1+2)/(1+2-3)", "Error in line #1: Division by zero.");

        assertProgramOutputHasError("out 1//1", "Error in line #1: Encountered \" \"/\" \"");
        assertProgramOutputHasError("out 1**1", "Error in line #1: Encountered \" \"*\" \"");
    }

    @Test
    public void power() {
        assertProgramOutput("out 2^10", "1024.0");
        assertProgramOutput("out 2.0^10", "1024.0");
        assertProgramOutput("out 2.0^10.0", "1024.0");
        assertProgramOutput("out 2^(-1)", "0.5");
        assertProgramOutput("out 2^-1", "0.5");
        assertProgramOutput("out 0.5^-1", "2.0");
        assertProgramOutput("out 2^(3^2)", "512.0");
        assertProgramOutput("out (2^3)^2", "64.0");
        assertProgramOutput("out (0^3)^2", "0.0");
        assertProgramOutput("out 0^0", "1.0");
        assertEquals(1.0, Math.pow(0,0));

        assertProgramOutput("out 0^-1", "Error in line #1: Division by zero.");
        assertProgramOutput("out 0^-1.1", "Error in line #1: Division by zero.");
        assertProgramOutput("out 0.0^-2.1", "Error in line #1: Division by zero.");
        assertProgramOutput("out (1+2+3-1-2-3)^(1.5-10)", "Error in line #1: Division by zero.");

        assertProgramOutputHasError("out 1^^1", "Error in line #1: Encountered \" \"^\" \"");
        assertProgramOutputHasError("out 2^2^2", "Error in line #1: Encountered \" \"^\" \"");
    }

    @Test
    public void complexExpressions() {
        assertProgramOutput("out (1 +2 + 3.2 - .1 -- 5 --.02) / (10 + 1.0 + 0.12 ^ (3-2.0))", "1.0");
        assertProgramOutput("out (1 * 2 * 3.2 * .1 / .5 - 4) ^ 2 - 5", "2.3984");
        assertProgramOutput("out 3^2 + 4^2 - 5 ^ 2", "0.0");
        assertProgramOutput("out 3^(1.0 + 10/10) + 4^(3.14 / (2.14 + 1) + 1) - 5 ^ 2 * 2", "-25.0");
    }

    @Test
    public void parentheses() {
        assertProgramOutput("out (1+2)", "3");
        assertProgramOutput("out ((1+2))", "3");
        assertProgramOutput("out ((((1)+((2)))))", "3");

        assertProgramOutputHasError("out ()", "Error in line #1: Encountered \" \")\" \"");
        assertProgramOutputHasError("out (", "Error in line #1: Encountered \" <EOL>");
        assertProgramOutputHasError("out )", "Error in line #1: Encountered \" \")\"");
        assertProgramOutputHasError("out ((1)", "Error in line #1: Encountered \" <EOL>");
    }
}