package com.borunovv.jetpreter.gui;

import com.borunovv.jetpreter.core.log.Log;
import com.borunovv.jetpreter.interpreter.InterpreterServer;

public class Controller {

    public static final int USER_INACTIVITY_TIMEOUT_MS = 1000;
    public static final int IDLE_PROCESSING_PERIOD_MS = 50;

    private final View view;
    private final InterpreterServer server = new InterpreterServer();
    private Task currentTask;
    private String pendingProgram;
    private long lastUserActivityTime = 0;
    private long lastIdleProcessedTime = 0;


    public Controller(View view) {
        this.view = view;
    }

    void setUp() {
        server.start();
    }

    void tearDown() {
        try {
            cancelCurrentTask();
            server.ensureStopped();
        } catch (InterruptedException e) {
            Log.error("Controller: Error while wait server stop", e);
        }
    }

    void onIdle() {
        boolean canProcessIdle = System.currentTimeMillis() - lastIdleProcessedTime >= IDLE_PROCESSING_PERIOD_MS;
        if (canProcessIdle) {
            updateCurrentProgress();
            startBackgroundInterpretationIfNeed();
            lastIdleProcessedTime = System.currentTimeMillis();
        }
    }

    void onSourceCodeChanged(String program) {
        lastUserActivityTime = System.currentTimeMillis();
        pendingProgram = program;
        cancelCurrentTask();
    }

    private void updateCurrentProgress() {
        if (currentTask != null) {
           view.setProgress(currentTask.getProgress());
        }
    }

    private void startBackgroundInterpretationIfNeed() {
        if (hasPendingProgram() && isUserInactive()) {
            cancelCurrentTask();
            currentTask = new Task().start(pendingProgram);
            pendingProgram = null;
        }
    }

    private boolean hasPendingProgram() {
        return pendingProgram != null;
    }

    private boolean isUserInactive() {
        long userInactivityTimeMs = System.currentTimeMillis() - lastUserActivityTime;
        return userInactivityTimeMs >= USER_INACTIVITY_TIMEOUT_MS;
    }

    private void cancelCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
            view.clearOutput();
            view.setProgress(0);
        }
    }

    private class Task {
        private InterpreterServer.IProgramTask serverTask;
        private volatile boolean disableOutput;

        Task() {
        }

        Task start(String program) {
            this.serverTask = server.submitProgram(program, this::writeToOutput, this::writeToErrors);
            return this;
        }

        void cancel() {
            disableOutput = true;
            serverTask.cancel();
        }

        double getProgress() {
            return serverTask.getProgress();
        }

        void writeToOutput(String str) {
            if (!disableOutput) {
                view.appendOutput(str);
            }
        }

        void writeToErrors(String error) {
            if (!disableOutput) {
                view.appendOutputFromNewLine(error);
            }
        }
    }
}
