package com.borunovv.jetpreter;

import com.borunovv.jetpreter.interpreter.Interpreter;
import com.borunovv.jetpreter.interpreter.InterpreterSession;

public class Main {

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

    public static void main(String[] args) throws Exception {
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

        if (verifySuccess) {
            System.out.println("\nOutput:\n");
            InterpreterSession session = interpreter.startInterpretation(System.out::print);
            for (int i = 0; i < session.linesCount(); ++i) {
                int lineNumberInSourceCode = session.getNextLineNumberInSourceCode();
                String error = session.interpretNextLine();
                if (error != null) {
                    System.out.println("\n\n>>Error in line #" + lineNumberInSourceCode + ": " + error);
                    break;
                }
            }
        }

//        StringBuilder sb = new StringBuilder();
//        astRoot.dump(sb, 0);
//        System.out.println(sb.toString());
    }

    private static void showProgram(String program) {
        String[] items = program.split("\n");
        for (int i = 0; i < items.length; i++) {
            String line = items[i].trim();
            System.out.println((i + 1) + "\t" + line);
        }
    }
}