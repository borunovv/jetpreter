package com.borunovv.jetpreter.interpreter;

import java.util.ArrayList;

/**
 * @author borunovv
 */
public class ArrayListStack<T> {

    private ArrayList<T> array = new ArrayList<>();
    private int stackPointer = -1;

    public void push(T item) {
        stackPointer++;
        if (stackPointer == array.size()) {
            array.add(item);
        } else {
            array.set(stackPointer, item);
        }
    }

    public T pop() {
        if (stackPointer < 0) {
            throw new IllegalStateException("Stack is empty");
        }
        return array.set(stackPointer--, null);
    }

    public int size() {
        return stackPointer + 1;
    }

    public void clear() {
        array = new ArrayList<>();
        stackPointer = -1;
    }
}
