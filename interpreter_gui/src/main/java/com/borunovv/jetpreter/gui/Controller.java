package com.borunovv.jetpreter.gui;

import com.borunovv.jetpreter.core.log.Log;
import com.borunovv.jetpreter.interpreter.InterpreterServer;

public class Controller {

    public static final int USER_TYPING_IDLE_TIMEOUT_MS = 1000;

    private Model model;
    private InterpreterServer server = new InterpreterServer();
    private Task currentTask;
    private String pendingProgram;
    private long lastUserTypingTime = 0;


    public Controller(Model model) {
        this.model = model;
    }

    void setUp() {
        server.start();
    }

    void tearDown() {
        try {
            server.stopAndWait();
        } catch (InterruptedException e) {
            Log.error("Controller: Error while wait server stop", e);
        }
    }

    void onIdle() {
        if (currentTask != null) {
            model.setProgress(currentTask.getProgress());
        }

        if (System.currentTimeMillis() - lastUserTypingTime > USER_TYPING_IDLE_TIMEOUT_MS) {
            if (pendingProgram != null) {
                cancelCurrentTask();
                model.clearOutput();
                currentTask = new Task().start(pendingProgram);
                pendingProgram = null;
            }
        }
    }

    void onSourceCodeChanged(String program) {
        lastUserTypingTime = System.currentTimeMillis();
        pendingProgram = program;
    }

    private void cancelCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
            model.setProgress(0);
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
                model.appendOutput(str);
            }
        }

        void writeToErrors(String error) {
            if (!disableOutput) {
                model.appendOutputFromNewLine(error);
            }
        }
    }
}
