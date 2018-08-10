package com.borunovv.jetpreter.interpreter;

import java.util.List;

/**
 * @author borunovv
 */
public class InterpreterSession {

    private final List<ProgramLine> lines;
    private final Context ctx;
    private int nextLine = 0;

    public InterpreterSession(List<ProgramLine> lines, Context ctx) {
        this.lines = lines;
        this.ctx = ctx;
    }

    public int linesCount() {
        return lines.size();
    }

    public boolean hasNextLine() {
        return nextLine < linesCount();
    }

    public void interpretNextLine() {
        if (! hasNextLine()) {
            throw new InterpretException("No more lines to interpret!");
        }
        lines.get(nextLine).interpret(ctx);
        nextLine++;
    }
}
