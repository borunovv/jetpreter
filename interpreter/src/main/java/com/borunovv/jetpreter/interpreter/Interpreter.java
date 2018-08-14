package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Program interpreter.
 *
 * @author borunovv
 */
public class Interpreter {
    /**
     * Cache of code lines. Used to speed up program updating.
     */
    private Map<String, ProgramLine> cache = new HashMap<>();

    /**
     * Current program lines of code.
     */
    private List<ProgramLine> lines = new ArrayList<>();

    Interpreter() {
    }

    /**
     * Update current program.
     *
     * @param program      New program
     * @param cancelSignal Source of cancellation signal
     */
    public void updateProgram(String program, CancelSignal cancelSignal) {
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

            if (cancelSignal != null && cancelSignal.isCanceled()) {
                lines.clear();
                throw new CancelException(lineNumberInSourceCode, "Canceled during program updating");
            }
        }
        cache = newCache;
    }

    /**
     * Return program lines count (excluding empty lines).
     */
    public int linesCount() {
        return lines.size();
    }

    /**
     * Start program interpretation.
     *
     * @param output       Program output (a.k.a 'stdout')
     * @param cancelSignal Source of cancellation signal
     * @return interpretation session
     */
    public InterpreterSession startInterpretation(Consumer<String> output, CancelSignal cancelSignal) {
        return new InterpreterSession(
                new ArrayList<>(lines),
                new Context(output, cancelSignal));
    }
}
