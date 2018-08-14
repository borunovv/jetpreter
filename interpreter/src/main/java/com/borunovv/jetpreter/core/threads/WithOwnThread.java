package com.borunovv.jetpreter.core.threads;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Base class for async execution. Wraps the {@link Thread}.
 *
 * @author borunovv
 */
public abstract class WithOwnThread implements Closeable {

    public enum ThreadState {STOPPED, RUNNING, STOP_REQUESTED, STOPPING}

    private AtomicReference<ThreadState> threadState = new AtomicReference<>(ThreadState.STOPPED);

    /**
     * Start the worker thread.
     */
    public void start() {
        if (threadState.compareAndSet(ThreadState.STOPPED, ThreadState.RUNNING)) {
            new Thread(this::doInThread).start();
        }
    }

    /**
     * Ask to stop the worker thread. Nonblocking.
     */
    public void stop() {
        threadState.compareAndSet(ThreadState.RUNNING, ThreadState.STOP_REQUESTED);
    }

    /**
     * Return true if worker thread is running.
     */
    protected boolean isRunning() {
        return threadState.get() == ThreadState.RUNNING;
    }

    /**
     * Ask to stop the worker thread. Blocking. Wait until thread stopped.
     *
     * @throws InterruptedException
     */
    public void ensureStopped() throws InterruptedException {
        if (threadState.compareAndSet(ThreadState.RUNNING, ThreadState.STOP_REQUESTED)) {
            while (threadState.get() != ThreadState.STOPPED) {
                Thread.sleep(1);
            }
        }
    }

    /**
     * Same as {@link #stop()}. Ask to stop the worker thread. Nonblocking.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        stop();
    }

    /**
     * Return true if we was asked to stop the worker thread,
     * i.e. {@link #stop()} or {@link #ensureStopped} or {@link #close()} was called.
     */
    protected boolean isStopRequested() {
        return threadState.get() == ThreadState.STOP_REQUESTED;
    }

    /**
     * This method executes in worker thread.
     */
    private void doInThread() {
        try {
            onThreadStart();
            while (!threadState.compareAndSet(ThreadState.STOP_REQUESTED, ThreadState.STOPPING)) {
                onThreadIteration();
            }
        } catch (Throwable t) {
            onThreadError(t);
        } finally {
            threadState.set(ThreadState.STOPPED);
            onThreadStop();
        }
    }

    /**
     * Do sleep with continuous checking for stop request.
     *
     * @param milliseconds millis to sleep
     */
    protected void sleep(long milliseconds) {
        try {
            long start = System.currentTimeMillis();
            while (!isStopRequested()) {
                long elapsed = milliseconds - (System.currentTimeMillis() - start);
                if (elapsed <= 0) {
                    break;
                }
                Thread.sleep(Math.min(10, elapsed));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Template method to override in descendants.
     * Called once at worker thread start.
     */
    protected abstract void onThreadStart();

    /**
     * Template method to override in descendants.
     * Called once at worker thread stop.
     */
    protected abstract void onThreadStop();

    /**
     * Template method to override in descendants.
     * Called on worker thread unhandled error occurs.
     */
    protected abstract void onThreadError(Throwable e);

    /**
     * Template method to override in descendants.
     * Called periodically from worker thread to do useful stuff.
     */
    protected abstract void onThreadIteration();
}
