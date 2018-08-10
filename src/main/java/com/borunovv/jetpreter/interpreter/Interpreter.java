package com.borunovv.jetpreter.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author borunovv
 */
public class Interpreter {
    private List<ProgramLine> lines = new ArrayList<>();

    public Interpreter() {
    }

    public Interpreter(String program) {
        updateProgram(program);
    }

    public void updateProgram(String program) {
        String[] items = program.split("\n");
        for (int i = 0; i < items.length; i++) {
            String line = items[i].trim();
            if (!line.isEmpty()) {
                if (i < lines.size()) {
                    lines.get(i).update(line);
                } else {
                    lines.add(new ProgramLine(line, i + 1));
                }
            }
        }

        if (items.length < lines.size()) {
            lines = lines.subList(0, items.length);
        }
    }

    public int linesCount() {
        return lines.size();
    }

    public String verifyLine(int lineIndex) {
        return lines.get(lineIndex).verify();
    }

    public int getLineNumberInSourceCode(int lineIndex) {
        return lines.get(lineIndex).getLineNumberInSourceCode();
    }

    public InterpreterSession startInterpretation(Consumer<String> output) {
        return new InterpreterSession(new ArrayList<>(lines), new Context(output));
    }
}
