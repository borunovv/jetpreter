package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author borunovv
 */
public class Interpreter {
    private Map<String, ProgramLine> cache = new HashMap<>();
    private List<ProgramLine> lines = new ArrayList<>();

    public Interpreter() {
    }

    public Interpreter(String program) {
        updateProgram(program);
    }

    public void updateProgram(String program) {
        String[] items = StringUtils.ensureString(program).split("\n");
        Map<String, ProgramLine> newCache = new HashMap<>();
        lines = new ArrayList<>(items.length);

        for (int i = 0; i < items.length; i++) {
            String line = items[i].trim();
            if (line.isEmpty()) continue;

            int lineNumberInSourceCode = i + 1;
            ProgramLine programLine = cache.get(line);
            if (programLine == null) {
                programLine = new ProgramLine(line, lineNumberInSourceCode);
            } else {
                programLine.setLineNumberInSourceCode(lineNumberInSourceCode);
            }
            lines.add(programLine);
            newCache.put(line, programLine);
        }
        cache = newCache;
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
