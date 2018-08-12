package com.borunovv.jetpreter.core.threads;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public abstract class WithOwnThread implements Closeable {

    public enum ThreadState {STOPPED, RUNNING, STOP_REQUESTED, STOPPING}

    private AtomicReference<ThreadState> threadState = new AtomicReference<>(ThreadState.STOPPED);

    public void start() {
        if (threadState.compareAndSet(ThreadState.STOPPED, ThreadState.RUNNING)) {
            new Thread(this::doInThread).start();
        }
    }

    public void stop() {
        threadState.compareAndSet(ThreadState.RUNNING, ThreadState.STOP_REQUESTED);
    }

    protected boolean isRunning() {
        return threadState.get() == ThreadState.RUNNING;
    }

    public void stopAndWait() throws InterruptedException {
        if (threadState.compareAndSet(ThreadState.RUNNING, ThreadState.STOP_REQUESTED)) {
            while (threadState.get() != ThreadState.STOPPED) {
                Thread.sleep(1);
            }
        }
    }

    @Override
    public void close() throws IOException {
        stop();
    }

    protected boolean isStopRequested() {
        return threadState.get() == ThreadState.STOP_REQUESTED;
    }

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

    protected abstract void onThreadStop();

    protected abstract void onThreadError(Throwable e);

    protected abstract void onThreadStart();

    protected abstract void onThreadIteration();
}
