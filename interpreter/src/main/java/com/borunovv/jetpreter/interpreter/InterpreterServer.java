package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.threads.WithOwnThread;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class InterpreterServer extends WithOwnThread {
    public enum State {READY, PROCESSING}

    private volatile State state = State.READY;

    private final AtomicReference<ProgramTask> pendingProgramRef = new AtomicReference<>(null);
    private final Interpreter interpreter = new Interpreter();
    private ProcessingState verificationState;
    private ProgramTask currentProgramTask;
    private volatile int debugDelayMillisPerLine = 0;

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
        System.out.println("[DEBUG] InterpreterServer: started");
    }

    @Override
    protected void onThreadStop() {
        reset();
        cancelCurrentTask();
        currentProgramTask = tryGetPendingProgramTask();
        cancelCurrentTask();
        System.out.println("[DEBUG] InterpreterServer: stopped");
    }

    private void reset() {
        state = State.READY;
        pendingProgramRef.set(null);
        interpreter.updateProgram(null);
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
                && (hasNewTask || currentProgramTask.isCancelled());

        if (needCancelCurrentTask) {
            cancelCurrentTask();
        }

        switch (state) {
            case READY: {
                if (hasNewTask) {
                    currentProgramTask = tryGetPendingProgramTask();
                    if (currentProgramTask != null) {
                        interpreter.updateProgram(currentProgramTask.getProgram());
                        startVerification(currentProgramTask.getOutputConsumer(), currentProgramTask.getErrorsConsumer());
                        state = State.PROCESSING;
                    }
                }
                break;
            }
            case PROCESSING: {
                if (needCancelCurrentTask) {
                    stopVerification();
                    state = State.READY;
                } else {
                    boolean finished = !continueVerification();
                    if (finished) {
                        stopVerification();
                        completeCurrentTask();
                        state = State.READY;
                    } else {
                        currentProgramTask.setProgress(verificationState.getProgress());
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
            System.out.println("[DEBUG] Current task cancelled.");
            currentProgramTask = null;
        }
    }

    private void completeCurrentTask() {
        currentProgramTask.complete();
        currentProgramTask = null;
    }

    private boolean continueVerification() {
        // Used for debug slow down.
        if (debugDelayMillisPerLine > 0) {
            sleep(debugDelayMillisPerLine);
        }
        return verificationState != null && verificationState.processNextLine();
    }

    private void startVerification(Consumer<String> output, Consumer<String> errors) {
        verificationState = new ProcessingState(output, errors);
    }

    private void stopVerification() {
        verificationState = null;
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

    private class ProcessingState {
        private final Consumer<String> errors;
        private int linesProcessed = 0;
        private final InterpreterSession session;
        private String lastError;

        public ProcessingState(Consumer<String> output, Consumer<String> errors) {
            this.errors = errors;
            this.session = interpreter.startInterpretation(output);
        }

        public boolean processNextLine() {
            if (canContinue()) {
                int lineNumberInSource = session.getNextLineNumberInSourceCode();
                String error = session.interpretNextLine();
                linesProcessed++;
                if (error != null) {
                    lastError = "Error in line #" + lineNumberInSource + ": " + error;
                    errors.accept(lastError + "\n");
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

    public interface IProgramTask {
        boolean isFinished();
        boolean isCompleted();
        boolean isCancelled();
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
        public boolean isCancelled() {
            return cancelledFlag.get();
        }

        @Override
        public boolean isCompleted() {
            return completedFlag.get();
        }

        @Override
        public boolean isFinished() {
            return isCompleted() || isCancelled();
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
