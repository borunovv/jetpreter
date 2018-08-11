package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.threads.WithOwnThread;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class InterpreterServer extends WithOwnThread {
    public enum State {READY, VERIFICATION}

    private volatile State state = State.READY;

    private final AtomicReference<ProgramTask> pendingProgramRef = new AtomicReference<>();
    private final Interpreter interpreter = new Interpreter();
    private VerificationState verificationState;
    private ProgramTask currentProgramTask;
    private volatile int debugDelayMillisPerLine = 0;

    public InterpreterServer() {
    }

    public IProgramTask submitProgram(String program, Consumer<String> errorsConsumer) {
        if (!isRunning()) throw new RuntimeException("InterpreterServer is stopped.");

        ProgramTask task = new ProgramTask(program, errorsConsumer);

        while (!isStopRequested()) {
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
        reset();
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
            currentProgramTask.getErrorsConsumer().accept(e.getMessage());
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
                        startVerification(currentProgramTask.getErrorsConsumer());
                        state = State.VERIFICATION;
                    }
                }
                break;
            }
            case VERIFICATION: {
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
        if (verificationState == null || !verificationState.hasNextLine()) {
            return false;
        }

        String error = verificationState.verifyNextLine();
        if (error != null) {
            currentProgramTask.getErrorsConsumer().accept(error);
        }

        boolean canContinue = (error == null) && verificationState.hasNextLine();

        // Used for debug slow down.
        if (debugDelayMillisPerLine > 0) {
            sleep(debugDelayMillisPerLine);
        }
        return canContinue;
    }

    private void startVerification(Consumer<String> errorConsumer) {
        verificationState = new VerificationState();
    }

    private void stopVerification() {
        verificationState = null;
    }

    private ProgramTask tryGetPendingProgramTask() {
        while (!isStopRequested()) {
            ProgramTask task = pendingProgramRef.get();
            if (task == null) {
                return task;
            }

            if (pendingProgramRef.compareAndSet(task, null)) {
                return task;
            }
        }
        return null;
    }

    public void setDebugSlowDownPerLine(int delayMillisPerLine) {
        this.debugDelayMillisPerLine = delayMillisPerLine;
    }

    private class VerificationState {
        private int nextLine = 0;

        public String verifyNextLine() {
            String error = null;
            if (hasNextLine()) {
                String syntaxError = interpreter.verifyLine(nextLine);
                if (syntaxError != null) {
                    error = "Error in line #" + interpreter.getLineNumberInSourceCode(nextLine) + ": " + syntaxError;
                }
                nextLine++;
            }
            return error;
        }

        public boolean hasNextLine() {
            return nextLine < interpreter.linesCount();
        }

        public double getProgress() {
            int totalLines = interpreter.linesCount();
            return totalLines > 0 ?
                    (double) nextLine / totalLines :
                    1.0;
        }
    }

    public interface IProgramTask {
        boolean isRunning();
        boolean isCompleted();
        boolean isCancelled();
        void cancel();
        double getProgress();
    }

    private static class ProgramTask implements IProgramTask {
        private final Consumer<String> errorsConsumer;
        private final String program;
        private final AtomicBoolean completedFlag = new AtomicBoolean();
        private final AtomicBoolean cancelledFlag = new AtomicBoolean();
        private double progress = 0.0;

        public ProgramTask(String program, Consumer<String> errorsConsumer) {
            this.program = program;
            this.errorsConsumer = errorsConsumer;
        }

        public String getProgram() {
            return program;
        }

        public Consumer<String> getErrorsConsumer() {
            return errorsConsumer;
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
        public boolean isRunning() {
            return !isCompleted() && !isCancelled();
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
