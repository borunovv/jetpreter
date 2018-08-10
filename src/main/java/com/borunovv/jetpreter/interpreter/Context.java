package com.borunovv.jetpreter.interpreter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author borunovv
 */
public class Context {

    private static Set<String> KEY_WORDS = new HashSet<>();

    static {
        KEY_WORDS.add("var");
        KEY_WORDS.add("print");
        KEY_WORDS.add("out");
    }

    private ArrayListStack<Object> stack = new ArrayListStack<>();
    private Map<String, Object> variables = new HashMap<>();
    private Consumer<String> output;

    public Context(Consumer<String> output) {
        this.output = output;
    }

    public void push(Object item) {
        stack.push(item);
    }

    public Object pop() {
        return stack.pop();
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public void writeToOutput(String str) {
        output.accept(str);
    }

    public boolean isKeyWord(String name) {
        return KEY_WORDS.contains(name);
    }
}
