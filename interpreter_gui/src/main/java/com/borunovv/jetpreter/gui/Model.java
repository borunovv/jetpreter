package com.borunovv.jetpreter.gui;

public interface Model {
    void setProgress(double progress);
    void appendOutput(String text);
    void clearOutput();
    void appendOutputFromNewLine(String text);
}
