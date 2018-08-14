package com.borunovv.jetpreter.interpreter;

/**
 * Source of cancel signal.
 * Used to check cancellation during interpretation process.
 *
 * @author borunovv
 */
public interface CancelSignal {
    boolean isCanceled();
}
