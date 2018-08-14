package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.contract.Precondition;
import com.borunovv.jetpreter.interpreter.functions.Function;
import com.borunovv.jetpreter.interpreter.functions.FunctionMap;
import com.borunovv.jetpreter.interpreter.functions.FunctionReduce;
import com.borunovv.jetpreter.interpreter.types.Value;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author borunovv
 */
public class Context {

    private static Set<String> KEY_WORDS = new HashSet<>();
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

    private ArrayListStack<Value> stack = new ArrayListStack<>();
    private Map<String, Value> variables = new HashMap<>();
    private Consumer<String> output;
    private final CancelSignal cancelSignal;

    public Context(Consumer<String> output, CancelSignal cancelSignal) {
        this.output = output;
        this.cancelSignal = cancelSignal;
    }

    public void push(Value item) {
        checkCancel();
        stack.push(item);
    }

    public Value pop() {
        checkCancel();
        return stack.pop();
    }

    public boolean hasVariable(String name) {
        checkCancel();
        return variables.containsKey(name);
    }

    public void setVariable(String name, Value value) {
        checkCancel();
        variables.put(name, value);
    }

    public Value getVariable(String name) {
        checkCancel();
        return variables.get(name);
    }

    public void writeToOutput(String str) {
        checkCancel();
        output.accept(str);
    }

    public boolean isKeyWord(String name) {
        checkCancel();
        return KEY_WORDS.contains(name);
    }

    public Consumer<String> getOutput() {
        checkCancel();
        return output;
    }

    public Function getFunction(String name) {
        checkCancel();
        return functions.get(name);
    }

    public Context cloneForLambda() {
        return new Context(output, cancelSignal);
    }

    public void checkCancel() {
        if (cancelSignal != null && cancelSignal.isCanceled()) {
            throw new CancelException("Interpretation cancelled");
        }
    }
}
