package com.borunovv.jetpreter.interpreter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InterpreterServerProgramSequenceTest extends AbstractInterpreterServerTest {

    @Test
    public void performsSequenceOfValidPrograms() {
        // ARRANGE
        final String PROGRAM1 = "" +
                "print \"This is Program_1\\n\" \n" +
                "var x = 1 + 2 + 3 \n" +
                "var y = x ^ 2 \n" +
                "out y";

        final String PROGRAM2 = "" +
                "print \"This is Program_2\\n\" \n" +
                "var x = 1 + 5 - 2 \n" +
                "var y = x ^ 3\n" +
                "out y";

        final StringConsumer errorsConsumer = new StringConsumer();
        final StringConsumer outputConsumer = new StringConsumer();

        // ACT
        for (int i = 0; i < 10; ++i) {
            boolean first = (i % 2 == 0);
            String program = first ? PROGRAM1 : PROGRAM2;
            InterpreterServer.IProgramTask task = server.submitProgram(program, outputConsumer, errorsConsumer);
            waitCondition("Task must finish.", 10000, task::isFinished);
        }

        // VERIFY
        String expectedOutput = "" +
                "This is Program_1\n" +
                "36.0\n" +
                "This is Program_2\n" +
                "64.0\n" +
                "This is Program_1\n" +
                "36.0\n" +
                "This is Program_2\n" +
                "64.0\n" +
                "This is Program_1\n" +
                "36.0\n" +
                "This is Program_2\n" +
                "64.0\n" +
                "This is Program_1\n" +
                "36.0\n" +
                "This is Program_2\n" +
                "64.0\n" +
                "This is Program_1\n" +
                "36.0\n" +
                "This is Program_2\n" +
                "64.0\n";

        assertEquals(expectedOutput, outputConsumer.toString());
        assertTrue(errorsConsumer.toString().isEmpty());
    }

    @Test
    public void performsSequenceOfBuggyPrograms() {
        // ARRANGE
        final String PROGRAM1 = "" +
                "print \"This is Program_1 with bug in line #3\\n\" \n" +
                "var x = 1 + 2 + 3 \n" +
                "var y = x ^ 2 + bugbugbug \n" +
                "out y";

        final String PROGRAM2 = "" +
                "print \"This is Program_2 with bug in line #4\\n\" \n" +
                "var x = 1 + 5 - 2 \n" +
                "var y = x ^ 3\n" +
                "bugbugbug = 1";

        StringConsumer errors1 = new StringConsumer();
        StringConsumer output1 = new StringConsumer();

        StringConsumer errors2 = new StringConsumer();
        StringConsumer output2 = new StringConsumer();

        // ACT
        InterpreterServer.IProgramTask task1 = server.submitProgram(PROGRAM1, output1, errors1);
        waitCondition("Task #1 must finish.", 100000, task1::isFinished);
        InterpreterServer.IProgramTask task2 = server.submitProgram(PROGRAM2, output2, errors2);
        waitCondition("Task #2 must finish.", 10000, task2::isFinished);

        // VERIFY
        System.out.println("Output1:\n" + output1.toString());
        System.out.println("Errors1:\n" + errors1.toString());

        System.out.println("Output2:\n" + output2.toString());
        System.out.println("Errors2:\n" + errors2.toString());

        assertEquals("This is Program_1 with bug in line #3\n", output1.toString());
        assertEquals("Error in line #3: Undefined variable: 'bugbugbug'. Forgot to declare ?\n", errors1.toString());

        assertEquals("This is Program_2 with bug in line #4\n", output2.toString());
        assertEquals("Error in line #4: Undefined variable: 'bugbugbug'. Forgot to declare ?\n", errors2.toString());
    }
}