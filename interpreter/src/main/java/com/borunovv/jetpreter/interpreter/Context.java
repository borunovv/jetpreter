package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.core.contract.Precondition;
import com.borunovv.jetpreter.interpreter.functions.Function;
import com.borunovv.jetpreter.interpreter.functions.FunctionMap;
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
    }

    private static void registerFunction(FunctionMap function) {
        Precondition.expected(!functions.containsKey(function.getName()),
                "Function already registered: " + function.getName() + ".");
        functions.put(function.getName(), function);
    }

    private ArrayListStack<Value> stack = new ArrayListStack<>();
    private Map<String, Value> variables = new HashMap<>();
    private Consumer<String> output;


    public Context(Consumer<String> output) {
        this.output = output;
    }

    public void push(Value item) {
        stack.push(item);
    }

    public Value pop() {
        return stack.pop();
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    public void setVariable(String name, Value value) {
        variables.put(name, value);
    }

    public Value getVariable(String name) {
        return variables.get(name);
    }

    public void writeToOutput(String str) {
        output.accept(str);
    }

    public boolean isKeyWord(String name) {
        return KEY_WORDS.contains(name);
    }

    public Consumer<String> getOutput() {
        return output;
    }

    public Function getFunction(String name) {
        return functions.get(name);
    }
}
