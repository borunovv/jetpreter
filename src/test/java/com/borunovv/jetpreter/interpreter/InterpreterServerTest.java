package com.borunovv.jetpreter.interpreter;

import org.junit.Test;

import java.io.StringWriter;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class InterpreterServerTest {

    private static String PROGRAM = "" +
            "print \"Hello, world!\\n\" \n" +
            "var x = 5 + 6 +.1 - 1.5 * 3 / 2 ^ -2.1\n" +
            "var y = 3^2 + 1 \n" +
            "print \"result=\" \n" +
            "\n" +
            "\n" +
            "   \n " +
            "\n" +
            "out this_is_intentional_bug\n";


    @Test
    public void testCancellation() throws InterruptedException {
        final StringWriter output = new StringWriter();
        Consumer<String> outputConsumer = output::write;

        InterpreterServer server = new InterpreterServer();

        try {
            server.setDebugSlowDownPerLine(100);
            server.start();

            // Просто накидываем задачи одну за одной так часто, чтобы не успевал закончить и бросал.
            InterpreterServer.IProgramTask[] tasks = new InterpreterServer.IProgramTask[5];
            for (int i = 0; i < 5; ++i) {
                tasks[i] = server.submitProgram(PROGRAM, outputConsumer);
                while (tasks[i].getProgress() < 0.5) {
                    Thread.sleep(10);
                }
            }
            tasks[4].cancel();
            while (server.getState() != InterpreterServer.State.READY) {
                Thread.sleep(10);
            }
            for (int i = 0; i < 5; ++i) {
                assertTrue(tasks[i].isCancelled());
            }


            // Теперь дождемся пока наполовину отработает и отменим.
            InterpreterServer.IProgramTask task = server.submitProgram(PROGRAM, outputConsumer);
            while (task.getProgress() < 0.5) {
                Thread.sleep(10);
                System.out.println(String.format("Current progress: %.2f", task.getProgress() * 100));
            }
            task.cancel();
            while (server.getState() != InterpreterServer.State.READY) {
                Thread.sleep(10);
            }
            assertTrue(task.isCancelled());

            System.out.println("Output:\n" + output.toString());

            // До бага в последней строке не должно было дойти, т.к. таски все отменены.
            // Поэтому в output не должно быть ошибок.
            assertEquals("", output.toString());
        } finally {
            server.stopAndWait();
        }
    }
}