package com.borunovv.jetpreter.interpreter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InterpreterServerCancellationTest extends AbstractInterpreterServerTest {

    private static String PROGRAM_WITH_SYNTAX_ERROR_AT_LAST_LINE = "" +
            "print \"Hello, world!\\n\" \n" +
            "var x = 5 + 6 +.1 - 1.5 * 3 / 2 ^ -2.1\n" +
            "var y = 3^2 + 1 \n" +
            "print \"result=\" \n" +
            "this_is_intentional_bug\n";


    @Test
    public void cancelCurrentTaskAfterNewProgramSubmit() throws InterruptedException {
        // ARRANGE
        server.setDebugSlowDownPerLine(10);
        final StringConsumer errorsConsumer = new StringConsumer();
        InterpreterServer.IProgramTask[] tasks = new InterpreterServer.IProgramTask[5];

        // ACT
        for (int i = 0; i < 5; ++i) {
            tasks[i] = server.submitProgram(PROGRAM_WITH_SYNTAX_ERROR_AT_LAST_LINE, new DevNull(), errorsConsumer);
            final int index = i;
            waitCondition("Task progress reached 50%", 10000, () -> tasks[index].getProgress() >= 0.5);
        }
        tasks[4].cancel();
        waitCondition("Server ready", 10000, () -> server.getState() == InterpreterServer.State.READY);

        // VERIFY
        for (int i = 0; i < 5; ++i) {
            assertTrue(tasks[i].isCanceled());
        }
    }

    @Test
    public void cancelInTheMiddleOfWork() throws InterruptedException {
        // ARRANGE
        server.setDebugSlowDownPerLine(100);
        final StringConsumer errorsConsumer = new StringConsumer();

        // ACT
        InterpreterServer.IProgramTask task = server.submitProgram(
                PROGRAM_WITH_SYNTAX_ERROR_AT_LAST_LINE, new DevNull(), errorsConsumer);

        waitCondition("Task progress reached 50%", 10000, () -> task.getProgress() >= 0.5);
        task.cancel();
        waitCondition("Server ready", 10000, () -> server.getState() == InterpreterServer.State.READY);

        // VERIFY
        assertTrue(task.isCanceled());
        assertTrue(task.getProgress() < 1.0);

        // Expecting no errors because we cancelled task before interpreter has reached last line with bug.
        assertEquals("", errorsConsumer.toString());
    }
}