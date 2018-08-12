package com.borunovv.jetpreter.interpreter;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractInterpreterServerTest {
    protected InterpreterServer server = new InterpreterServer();

    @Before
    public void setUp() {
        server = new InterpreterServer();
        server.start();
    }

    @After
    public void tearDown() throws InterruptedException {
        server.stopAndWait();
    }

    protected void waitCondition(String conditionDescription, long millis, Callable<Boolean> condition) {
        long start = System.currentTimeMillis();

        try {
            while (System.currentTimeMillis() - start < millis && !condition.call()) {
                Thread.sleep(1);
            }
            if (! condition.call()) {
                throw new RuntimeException("Wait timeout (" + millis + " ms). Condition: " + conditionDescription);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}