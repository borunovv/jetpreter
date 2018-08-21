package com.borunovv.jetpreter.gui;

import com.borunovv.jetpreter.core.log.Log;
import com.borunovv.jetpreter.interpreter.InterpreterServer;

/**
 * Main controller.
 * Listens GUI state and perform background program interpretation.
 */
public class Controller {
    /**
     * If user not active during 1 sec, we will start background task.
     */
    private static final int USER_INACTIVITY_TIMEOUT_MS = 1000;

    /**
     * GUI idle processing period in milliseconds.
     * Used To reduce CPU load.
     */
    private static final int IDLE_PROCESSING_PERIOD_MS = 50;

    /**
     * GUI view
     */
    private final View view;

    /**
     * Interpreter server to perform background interpretation.
     */
    private final InterpreterServer server = new InterpreterServer();

    /**
     * Current background task
     */
    private Task currentTask;

    /**
     * Last fresh program waiting to be interpreted in background.
     */
    private String pendingProgram;

    /**
     * Last user activity time.
     */
    private long lastUserActivityTime = 0;

    /**
     * Last GUI idle processing time.
     */
    private long lastIdleProcessedTime = 0;


    /**
     * C-tor.
     *
     * @param view the GUI as view.
     */
    public Controller(View view) {
        this.view = view;
    }

    /**
     * Called once on application start.
     */
    void setUp() {
        server.start();
    }

    /**
     * Called once on application close.
     */
    void tearDown() {
        try {
            cancelCurrentTask();
            server.ensureStopped();
        } catch (InterruptedException e) {
            Log.error("Controller: Error while wait server stop", e);
        }
    }

    /**
     * Called periodically on GUI idle.
     */
    void onIdle() {
        boolean canProcessIdle = System.currentTimeMillis() - lastIdleProcessedTime >= IDLE_PROCESSING_PERIOD_MS;
        if (canProcessIdle) {
            updateCurrentProgress();
            startBackgroundInterpretationIfNeed();
            lastIdleProcessedTime = System.currentTimeMillis();
        }
    }

    /**
     * Called every time user changed program source code.
     *
     * @param program current (changed) program
     */
    void onSourceCodeChanged(String program) {
        lastUserActivityTime = System.currentTimeMillis();
        pendingProgram = program;
        cancelCurrentTask();
    }

    /**
     * Update current background task progress on the view (GUI).
     */
    private void updateCurrentProgress() {
        if (currentTask != null) {
            view.setProgress(currentTask.getProgress());
        }
    }

    /**
     * Start new background interpretation task.
     */
    private void startBackgroundInterpretationIfNeed() {
        if (hasPendingProgram() && isUserInactive()) {
            cancelCurrentTask();
            currentTask = new Task().start(pendingProgram);
            pendingProgram = null;
        }
    }

    /**
     * Return true if there is fresh pending program waiting to be interpreted in background.
     */
    private boolean hasPendingProgram() {
        return pendingProgram != null;
    }

    /**
     * Return true is user can be qualified as 'inactive'
     * (i.e. user didn't changed program for last 1 second).
     */
    private boolean isUserInactive() {
        long userInactivityTimeMs = System.currentTimeMillis() - lastUserActivityTime;
        return userInactivityTimeMs >= USER_INACTIVITY_TIMEOUT_MS;
    }

    /**
     * Cancel current background task.
     */
    private void cancelCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
            view.clearOutput();
            view.setProgress(0);
        }
    }

    /**
     * Background task.
     */
    private class Task {
        /**
         * Interpreter's Program task object to track interpretation state.
         */
        private InterpreterServer.IProgramTask serverTask;

        /**
         * Flag to disallow writing into any output.
         * Used after cancellation to prevent updating GUI from cancelling tasks.
         */
        private volatile boolean disableOutput;

        Task() {
        }

        /**
         * Start interpretation in background.
         *
         * @param program program to interpret
         * @return self
         */
        Task start(String program) {
            this.serverTask = server.submitProgram(program, this::writeToOutput, this::writeToErrors);
            return this;
        }

        /**
         * Cancel background task.
         */
        void cancel() {
            disableOutput = true;
            serverTask.cancel();
        }

        /**
         * Return current background task progress [0..1]
         */
        double getProgress() {
            return serverTask.getProgress();
        }

        /**
         * Write string to program output (a.k.a 'stdout')
         * @param str string to write
         */
        void writeToOutput(String str) {
            if (!disableOutput) {
                view.appendOutput(str);
            }
        }

        /**
         * Write string to program error output (a.k.a 'stderr')
         * @param error error message
         */
        void writeToErrors(String error) {
            if (!disableOutput) {
                view.appendOutputFromNewLine(error);
            }
        }
    }
}
