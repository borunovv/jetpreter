package com.borunovv.jetpreter.gui;

/**
 * View functionality needed for controller.
 * Used to separate GUI code from controller logic.
 */
public interface View {
    void setProgress(double progress);
    void appendOutput(String text);
    void appendOutputFromNewLine(String text);
    void clearOutput();
}
