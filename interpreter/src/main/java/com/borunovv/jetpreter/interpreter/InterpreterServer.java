package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.log.Log;
import com.borunovv.jetpreter.core.threads.WithOwnThread;
import com.borunovv.jetpreter.core.util.SystemConstants;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Interpretation server.
 * Performs interpretation in separate thread.
 *
 * @author borunovv
 */
public class InterpreterServer extends WithOwnThread {
    /**
     * Server state.
     */
    public enum State {
        READY, PROCESSING
    }

    /**
     * Current server state.
     */
    private volatile State state = State.READY;

    /**
     * Holds last program to be interpreted.
     */
    private final AtomicReference<ProgramTask> pendingProgramRef = new AtomicReference<>(null);

    /**
     * Interpreter.
     */
    private final Interpreter interpreter = new Interpreter();

    /**
     * Current interpretation state.
     */
    private InterpretationState interpretationState;

    /**
     * Current program task.
     */
    private ProgramTask currentProgramTask;

    /**
     * For debug only.
     * Used to slow down interpretation process.
     */
    private int debugDelayMillisPerLine = 0;

    public InterpreterServer() {
    }

    /**
     * Submit new program (schedule to interpret).
     *
     * @param program Fresh program
     * @param output  Program output (a.k.a 'stdout')
     * @param errors  Program errors output (a.k.a 'stderr')
     * @return Program task
     */
    public IProgramTask submitProgram(String program, Consumer<String> output, Consumer<String> errors) {
        if (!isRunning()) {
            throw new RuntimeException("InterpreterServer is stopped.");
        }

        ProgramTask task = new ProgramTask(program, output, errors);

        while (isRunning()) {
            ProgramTask prevTask = pendingProgramRef.get();
            if (pendingProgramRef.compareAndSet(prevTask, task)) {
                if (prevTask != null) {
                    prevTask.cancel();
                }
                break;
            }
        }

        if (!isRunning()) {
            throw new RuntimeException("InterpreterServer is stopped.");
        }
        return task;
    }

    /**
     * Return current server state.
     */
    public State getState() {
        return state;
    }

    /**
     * Template method.
     * Called once at worker thread start.
     */
    @Override
    protected void onThreadStart() {
        Log.trace("InterpreterServer: started");
    }

    /**
     * Template method.
     * Called once at worker thread stop.
     */
    @Override
    protected void onThreadStop() {
        reset();
        cancelCurrentTask();
        Log.trace("InterpreterServer: stopped");
    }

    private void reset() {
        state = State.READY;
        pendingProgramRef.set(null);
        interpreter.updateProgram(null, null);
    }

    /**
     * Template method.
     * Called on worker thread unhandled error occurs.
     */
    @Override
    protected void onThreadError(Throwable e) {
        if (currentProgramTask != null) {
            currentProgramTask.getErrorsConsumer().accept(e.getMessage() + SystemConstants.LINE_SEPARATOR);
        } else {
            Log.error("InterpreterServer: worker thread error.", e);
        }
    }

    /**
     * Template method.
     * Called periodically from worker thread
     * Here we perform all interpretation work.
     */
    @Override
    protected void onThreadIteration() {
        // Check if we have new program to interpret.
        boolean hasNewTask = pendingProgramRef.get() != null;
        boolean needCancelCurrentTask = currentProgramTask != null
                && (hasNewTask || currentProgramTask.isCanceled());

        // Cancel current interpretation task if necessary.
        if (needCancelCurrentTask) {
            cancelCurrentTask();
        }

        switch (state) {
            case READY: {
                if (hasNewTask) {
                    currentProgramTask = tryGetPendingProgramTask();
                    if (currentProgramTask != null) {
                        interpreter.updateProgram(currentProgramTask.getProgram(), currentProgramTask);
                        startInterpretation(
                                currentProgramTask.getOutputConsumer(),
                                currentProgramTask.getErrorsConsumer(),
                                currentProgramTask);
                        state = State.PROCESSING;
                    }
                }
                break;
            }
            case PROCESSING: {
                if (needCancelCurrentTask) {
                    stopInterpretation();
                    state = State.READY;
                } else {
                    boolean finished = !continueInterpretation();
                    if (finished) {
                        stopInterpretation();
                        completeCurrentTask();
                        state = State.READY;
                    } else {
                        currentProgramTask.setProgress(interpretationState.getProgress());
                    }
                }
                break;
            }
        }

        sleep(1); // To not load CPU much.
    }

    /**
     * Cancels current interpretation task.
     */
    private void cancelCurrentTask() {
        if (currentProgramTask != null) {
            currentProgramTask.cancel();
            currentProgramTask = null;
        }
    }

    /**
     * Mark current interpretation task as completed.
     */
    private void completeCurrentTask() {
        if (!currentProgramTask.isCanceled()) {
            currentProgramTask.complete();
            currentProgramTask = null;
        }
    }

    /**
     * Attempt to continue interpretation (if nas more code lines).
     *
     * @return true if still can continue interpretation (no program EOF reached).
     */
    private boolean continueInterpretation() {
        // Used for debug to slow down processing.
        if (debugDelayMillisPerLine > 0) {
            sleep(debugDelayMillisPerLine);
        }
        try {
            return interpretationState != null && interpretationState.processNextLine();
        } catch (CancelException ignore) {
            return false;
        }
    }

    /**
     * Starts current program interpretation.
     *
     * @param output       Program output (a.k.a 'stdout')
     * @param errors       Program errors output (a.k.a 'stderr')
     * @param cancelSignal Source of cancellation signal.
     */
    private void startInterpretation(Consumer<String> output, Consumer<String> errors, CancelSignal cancelSignal) {
        interpretationState = new InterpretationState(output, errors, cancelSignal);
    }

    /**
     * Stops current program interpretation.
     */
    private void stopInterpretation() {
        interpretationState = null;
    }

    /**
     * Attempts to get pending program (the last fresh program which have to be interpreted).
     *
     * @return Program task or null if no pending program exists.
     */
    private ProgramTask tryGetPendingProgramTask() {
        while (true) {
            ProgramTask task = pendingProgramRef.get();
            if (task == null) {
                return task;
            }
            if (pendingProgramRef.compareAndSet(task, null)) {
                return task;
            }
        }
    }

    /**
     * For debug only. Set the interpretation 'slow down' timeout per program line. In milliseconds.
     *
     * @param delayMillisPerLine Interpretation 'slow down' timeout per line in milliseconds.
     */
    public void setDebugSlowDownPerLine(int delayMillisPerLine) {
        this.debugDelayMillisPerLine = delayMillisPerLine;
    }

    /**
     * Holds current interpretation state.
     * Used to perform interpretation line by line.
     */
    private class InterpretationState {
        /**
         * Program errors output (a.k.a 'stderr').
         */
        private final Consumer<String> errors;

        /**
         * Processed program lines counter.
         */
        private int linesProcessed = 0;

        /**
         * Interpreter session.
         */
        private final InterpreterSession session;

        /**
         * Last interpretation error.
         */
        private String lastError;

        /**
         * C-tor
         *
         * @param output       Program output (a.k.a 'stdout')
         * @param errors       Program error output (a.k.a 'stderr')
         * @param cancelSignal Source of cancellation signal
         */
        public InterpretationState(Consumer<String> output, Consumer<String> errors, CancelSignal cancelSignal) {
            this.errors = errors;
            this.session = interpreter.startInterpretation(output, cancelSignal);
        }

        /**
         * Interpret next program line.
         *
         * @return true, if can continue interpretation
         * (i.e. no errors occurred, no cancellation and has more lines of code to process).
         */
        public boolean processNextLine() {
            if (canContinue()) {
                int lineNumberInSource = session.getNextLineNumberInSourceCode();
                String error = session.interpretNextLine();
                linesProcessed++;
                if (error != null) {
                    lastError = "Error in line #" + lineNumberInSource + ": " + error;
                    errors.accept(lastError + SystemConstants.LINE_SEPARATOR);
                }
            }
            return canContinue();
        }

        /**
         * Return true if can continue interpretation
         * (i.e. no errors occurred, no cancellation and has more lines of code to process).
         */
        public boolean canContinue() {
            return lastError == null && linesProcessed < interpreter.linesCount();
        }

        /**
         * Return current interpretation progress, value in range [0..1].
         */
        public double getProgress() {
            int totalLines = interpreter.linesCount();
            return lastError == null && totalLines > 0 ?
                    (double) linesProcessed / totalLines :
                    1.0;
        }
    }

    /**
     * Something like {@link java.util.concurrent.Future}
     * to track interpretation state from another thread (usually GUI).
     */
    public interface IProgramTask extends CancelSignal {
        boolean isFinished();

        boolean isCompleted();

        void cancel();

        double getProgress();
    }

    /**
     * Program interpretation task implementation.
     * Used to track interpretation state from another thread (usually GUI).
     */
    private static class ProgramTask implements IProgramTask {
        /**
         * Program output (a.k.a 'stdout').
         */
        private final Consumer<String> errors;

        /**
         * Program errors output (a.k.a 'stderr').
         */
        private final Consumer<String> output;

        /**
         * Program to interpret.
         */
        private final String program;

        /**
         * Completion flag. If true, then whole program was interpreted without errors and cancellation.
         */
        private final AtomicBoolean completedFlag = new AtomicBoolean();

        /**
         * Cancel flag. If true, then the interpretation process need to be cancelled or is already cancelled.
         */
        private final AtomicBoolean cancelledFlag = new AtomicBoolean();

        /**
         * Current interpretation progress [0..1].
         */
        private double progress = 0.0;

        /**
         * C-tor.
         *
         * @param program Program to interpret
         * @param output  Program output (a.k.a 'stdout').
         * @param errors  Program errors output (a.k.a 'stderr').
         */
        public ProgramTask(String program, Consumer<String> output, Consumer<String> errors) {
            this.program = program;
            this.output = output;
            this.errors = errors;
        }

        public String getProgram() {
            return program;
        }

        public Consumer<String> getOutputConsumer() {
            return output;
        }

        public Consumer<String> getErrorsConsumer() {
            return errors;
        }

        /**
         * Mark task as completed.
         */
        public void complete() {
            completedFlag.set(true);
            progress = 1.0;
        }

        public void setProgress(double progress) {
            this.progress = progress;
        }

        @Override
        public boolean isCanceled() {
            return cancelledFlag.get();
        }

        @Override
        public boolean isCompleted() {
            return completedFlag.get();
        }

        /**
         * Return true if task is finished (no more under processing, i.e completed or cancelled).
         */
        @Override
        public boolean isFinished() {
            return isCompleted() || isCanceled();
        }

        @Override
        public void cancel() {
            cancelledFlag.set(true);
        }

        @Override
        public double getProgress() {
            return progress;
        }
    }
}
