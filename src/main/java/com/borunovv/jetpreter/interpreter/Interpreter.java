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
        String[] items  = program.split("\n");
        for (int i = 0; i < items.length; i++) {
            String line = items[i].trim();
            if (i < lines.size()) {
                lines.get(i).update(line);
            } else {
                lines.add(new ProgramLine(line));
            }
        }

        if (items.length < lines.size()) {
            lines = lines.subList(0, items.length);
        }
    }

    public int linesCount() {
        return lines.size();
    }

    public String verifyLine(int index) {
        return lines.get(index).verify();
    }

    public InterpreterSession startInterpretation(Consumer<String> output) {
        return new InterpreterSession(new ArrayList<>(lines), new Context(output));
    }
}
