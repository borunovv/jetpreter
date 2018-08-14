package com.borunovv.jetpreter.interpreter;

import java.util.List;

/**
 * Interpretation session.
 * Holds current interpretation state.
 * Used to interpret program line by line.
 *
 * @author borunovv
 */
public class InterpreterSession {
    /**
     * Program as list of lines (only meaning lines, without empty lines).
     */
    private final List<ProgramLine> lines;

    /**
     * Interpretation context (with stack, var table, function list etc.)
     */
    private final Context ctx;

    /**
     * Next line to interpret.
     */
    private int nextLine = 0;

    /**
     * C-tor.
     *
     * @param lines program lines
     * @param ctx   Interpretation context
     */
    public InterpreterSession(List<ProgramLine> lines, Context ctx) {
        this.lines = lines;
        this.ctx = ctx;
    }

    /**
     * Return program lines count (only meaning lines, without empty lines).
     *
     * @return
     */
    public int linesCount() {
        return lines.size();
    }

    /**
     * Return true if next line available to be interpreted (i.e. no program EOF reached).
     */
    public boolean hasNextLine() {
        return nextLine < linesCount();
    }

    /**
     * Return next line number in original source code. Used for error messages.
     */
    public int getNextLineNumberInSourceCode() {
        ensureHasNextLine();
        return lines.get(nextLine).getLineNumberInSourceCode();
    }

    /**
     * Interpret next line.
     *
     * @return error string on error occured or null if interpretation was successful.
     */
    public String interpretNextLine() {
        ensureHasNextLine();
        String error = lines.get(nextLine).interpret(ctx);
        nextLine++;
        return error;
    }

    /**
     * Ensures that next line available (not program EOF reached yet).
     * If no next line available then throws {@link InterpretException}.
     */
    private void ensureHasNextLine() {
        if (!hasNextLine()) {
            throw new InterpretException("No more lines to interpret!");
        }
    }
}
