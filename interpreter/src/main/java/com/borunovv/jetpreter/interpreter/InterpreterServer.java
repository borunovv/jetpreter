package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.log.Log;
import com.borunovv.jetpreter.core.threads.WithOwnThread;
import com.borunovv.jetpreter.core.util.SystemConstants;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class InterpreterServer extends WithOwnThread {
    public enum State {READY, PROCESSING}

    private volatile State state = State.READY;

    private final AtomicReference<ProgramTask> pendingProgramRef = new AtomicReference<>(null);
    private final Interpreter interpreter = new Interpreter();
    private InterpretationState interpretationState;
    private ProgramTask currentProgramTask;
    private int debugDelayMillisPerLine = 0;

    public InterpreterServer() {
    }

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

    public State getState() {
        return state;
    }

    @Override
    protected void onThreadStart() {
        Log.trace("InterpreterServer: started");
    }

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

    @Override
    protected void onThreadError(Throwable e) {
        if (currentProgramTask != null) {
            currentProgramTask.getErrorsConsumer().accept(e.getMessage() + "/n");
        } else {
            e.printStackTrace(System.err);
        }
    }

    @Override
    protected void onThreadIteration() {
        boolean hasNewTask = pendingProgramRef.get() != null;
        boolean needCancelCurrentTask = currentProgramTask != null
                && (hasNewTask || currentProgramTask.isCanceled());

        if (needCancelCurrentTask) {
            cancelCurrentTask();
        }

        switch (state) {
            case READY: {
                if (hasNewTask) {
                    currentProgramTask = tryGetPendingProgramTask();
                    if (currentProgramTask != null) {
                        interpreter.updateProgram(currentProgramTask.getProgram(), currentProgramTask);
                        startVerification(currentProgramTask.getOutputConsumer(),
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

        sleep(1);
    }

    private void cancelCurrentTask() {
        if (currentProgramTask != null) {
            currentProgramTask.cancel();
            Log.trace("Current task cancelled.");
            currentProgramTask = null;
        }
    }

    private void completeCurrentTask() {
        currentProgramTask.complete();
        currentProgramTask = null;
    }

    private boolean continueInterpretation() {
        // Used for debug slow down.
        if (debugDelayMillisPerLine > 0) {
            sleep(debugDelayMillisPerLine);
        }
        try {
            return interpretationState != null && interpretationState.processNextLine();
        } catch (CancelException ignore) {
            return false;
        }
    }

    private void startVerification(Consumer<String> output, Consumer<String> errors, CancelSignal cancelSignal) {
        interpretationState = new InterpretationState(output, errors, cancelSignal);
    }

    private void stopInterpretation() {
        interpretationState = null;
    }

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

    public void setDebugSlowDownPerLine(int delayMillisPerLine) {
        this.debugDelayMillisPerLine = delayMillisPerLine;
    }

    private class InterpretationState {
        private final Consumer<String> errors;
        private int linesProcessed = 0;
        private final InterpreterSession session;
        private String lastError;

        public InterpretationState(Consumer<String> output, Consumer<String> errors, CancelSignal cancelSignal) {
            this.errors = errors;
            this.session = interpreter.startInterpretation(output, cancelSignal);
        }

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

        public boolean canContinue() {
            return lastError == null && linesProcessed < interpreter.linesCount();
        }

        public double getProgress() {
            int totalLines = interpreter.linesCount();
            return lastError == null && totalLines > 0 ?
                    (double) linesProcessed / totalLines :
                    1.0;
        }
    }

    public interface IProgramTask extends CancelSignal{
        boolean isFinished();
        boolean isCompleted();
        void cancel();
        double getProgress();
    }

    private static class ProgramTask implements IProgramTask {
        private final Consumer<String> errors;
        private final Consumer<String> output;
        private final String program;
        private final AtomicBoolean completedFlag = new AtomicBoolean();
        private final AtomicBoolean cancelledFlag = new AtomicBoolean();
        private double progress = 0.0;

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
