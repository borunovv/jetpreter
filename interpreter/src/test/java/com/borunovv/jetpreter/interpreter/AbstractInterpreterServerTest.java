package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.ast.ASTNode;
import com.borunovv.jetpreter.ast.ASTNodeFactory;
import com.borunovv.jetpreter.javacc.generated.ParseException;
import com.borunovv.jetpreter.javacc.generated.ProgramParser;
import org.junit.After;
import org.junit.Before;

import java.io.StringReader;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public abstract class AbstractInterpreterServerTest {
    protected static final int DEFAULT_WAIT_TIMEOUT_MS = 10 * 1000; // 10 sec

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

    protected void interpretProgram(String program, Consumer<String> output, Consumer<String> errors, int waitTimeoutMs) {
        InterpreterServer.IProgramTask task = server.submitProgram(program, output, errors);
        waitCondition("Program interpretation", waitTimeoutMs, task::isFinished);
    }

    protected String interpretProgram(String program, int waitTimeoutMs) {
        StringConsumer outputAndErrors = new StringConsumer();
        interpretProgram(program, outputAndErrors, outputAndErrors, waitTimeoutMs);
        return outputAndErrors.toString();
    }

    protected String interpretProgram(String program) {
        StringConsumer outputAndErrors = new StringConsumer();
        interpretProgram(program, outputAndErrors, outputAndErrors, DEFAULT_WAIT_TIMEOUT_MS);
        return outputAndErrors.toString();
    }

    protected void assertProgramOutput(String program, String expectedOutput) {
        String output = interpretProgram(program);
        assertEquals(
                expectedOutput.trim().replaceAll("\r\n", "\n"),
                output.trim().replaceAll("\r\n", "\n"));
    }

    protected String renderASTTree(String program) {
        if (!program.endsWith("\n")) {
            program = program + "\n";
        }
        ProgramParser parser = new ProgramParser(new StringReader(program));
        try {
            parser.Program();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ASTNode root = ASTNodeFactory.buildTree(parser.rootNode());
        StringBuilder sb = new StringBuilder();
        root.dump(sb);
        return sb.toString();
    }
}