package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.contract.Precondition;
import com.borunovv.jetpreter.interpreter.functions.Function;
import com.borunovv.jetpreter.interpreter.functions.FunctionMap;
import com.borunovv.jetpreter.interpreter.functions.FunctionReduce;
import com.borunovv.jetpreter.interpreter.types.Value;

import java.util.*;
import java.util.function.Consumer;

/**
 * Interpretation context.
 * Holds stack, variable table and function list.
 *
 * @author borunovv
 */
public class Context {

    /**
     * Reserved keywords (like 'var', 'print', 'out'.
     */
    private static Set<String> KEY_WORDS = new HashSet<>();

    /**
     * All available functions like 'map', 'reduce'.
     */
    private static Map<String, Function> functions = new TreeMap<>();

    static {
        KEY_WORDS.add("var");
        KEY_WORDS.add("print");
        KEY_WORDS.add("out");

        registerFunction(new FunctionMap());
        registerFunction(new FunctionReduce());
    }

    private static void registerFunction(Function function) {
        Precondition.expected(!functions.containsKey(function.getName()),
                "Function already registered: " + function.getName() + ".");
        functions.put(function.getName(), function);
    }

    /**
     * Program stack.
     */
    private ArrayListStack<Value> stack = new ArrayListStack<>();

    /**
     * Variable table.
     */
    private Map<String, Value> variables = new HashMap<>();

    /**
     * Program output (a.k.a stdout).
     */
    private Consumer<String> output;

    /**
     * Source if cancellation signal.
     * Checked periodically inside interpreter to abort interpretation process.
     */
    private final CancelSignal cancelSignal;

    /**
     * C-rot
     *
     * @param output       rogram output (a.k.a stdout).
     * @param cancelSignal Source if cancellation signal.
     *                     Checked periodically inside interpreter to abort interpretation process.
     */
    public Context(Consumer<String> output, CancelSignal cancelSignal) {
        this.output = output;
        this.cancelSignal = cancelSignal;
    }

    /**
     * Push item onto the program stack.
     *
     * @param item value to push
     */
    public void push(Value item) {
        checkCancel();
        stack.push(item);
    }

    /**
     * Pop item from the stack.
     *
     * @return top element from the program stack
     */
    public Value pop() {
        checkCancel();
        return stack.pop();
    }

    /**
     * Return true if variable table contains given variable.
     */
    public boolean hasVariable(String name) {
        checkCancel();
        return variables.containsKey(name);
    }

    /**
     * Set the variable value (in variable table). Create new if not exist.
     */
    public void setVariable(String name, Value value) {
        checkCancel();
        variables.put(name, value);
    }

    /**
     * Return the variable value from variable table.
     *
     * @param name variable name
     */
    public Value getVariable(String name) {
        checkCancel();
        return variables.get(name);
    }

    /**
     * Write string to program output (like printing into 'stdout' in console applications).
     *
     * @param str string to write
     */
    public void writeToOutput(String str) {
        checkCancel();
        output.accept(str);
    }

    /**
     * Checks if given string is a reserved keyword.
     *
     * @param str string to check
     * @return truem if given string is a reserved keyword.
     */
    public boolean isKeyWord(String str) {
        checkCancel();
        return KEY_WORDS.contains(str);
    }

    /**
     * Return program output.
     */
    public Consumer<String> getOutput() {
        checkCancel();
        return output;
    }

    /**
     * Return function (like map/reduce) by name.
     *
     * @param name name of the function
     */
    public Function getFunction(String name) {
        checkCancel();
        return functions.get(name);
    }

    /**
     * Return new Context for lambda scope (with own stack and variable table).
     *
     * @return
     */
    public Context cloneForLambda() {
        return new Context(output, cancelSignal);
    }

    /**
     * Perform cancel signal cancel signal check.<br/>
     * Throws {@link CancelException} if interpretation was cancelled.
     */
    public void checkCancel() {
        if (cancelSignal != null && cancelSignal.isCanceled()) {
            throw new CancelException("Interpretation cancelled");
        }
    }
}
