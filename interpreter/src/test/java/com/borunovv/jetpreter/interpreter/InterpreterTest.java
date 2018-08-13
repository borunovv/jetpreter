package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.util.SystemConstants;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {

    private static String PROGRAM = "" +
            "print \"Hello, world!\\n\" \n" +
            "var x = 5 + 6 +.1 - 1.5 * 3 / 2 ^ -2.1\n" +
            "var y = 3^2 + 1 \n" +
            "print \"result=\" \n" +
            "\n" +
            "\n" +
            "   \n " +
            "\n" +
            "out x * x ^ 2 * y / 1\n";

    @Test
    public void simpleProgram() {
        showProgram(PROGRAM);
        System.out.println("");

        Interpreter interpreter = new Interpreter(PROGRAM);
        boolean verifySuccess = true;
        for (int i = 0; i < interpreter.linesCount(); ++i) {
            String error = interpreter.verifyLine(i);
            if (error != null) {
                System.out.println(">>Error in line #" + interpreter.getLineNumberInSourceCode(i) + ": " + error);
                verifySuccess = false;
            }
        }

        final StringWriter outputBuffer = new StringWriter();

        if (verifySuccess) {
            InterpreterSession session = interpreter.startInterpretation(outputBuffer::write);
            for (int i = 0; i < session.linesCount(); ++i) {
                int lineNumberInSourceCode = session.getNextLineNumberInSourceCode();
                String error = session.interpretNextLine();
                if (error != null) {
                    System.out.println("\n\n>>Error in line #" + lineNumberInSourceCode + ": " + error);
                    break;
                }
            }
        }
        String expected = "Hello, world!" + SystemConstants.LINE_SEPARATOR +
                "result=-5497.401761247628" + SystemConstants.LINE_SEPARATOR;

        assertEquals(expected, outputBuffer.toString());
    }

    private static void showProgram(String program) {
        String[] items = program.split("\n");
        for (int i = 0; i < items.length; i++) {
            String line = items[i].trim();
            System.out.println((i + 1) + "\t" + line);
        }
    }
}