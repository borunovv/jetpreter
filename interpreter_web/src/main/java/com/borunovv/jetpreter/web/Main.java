package com.borunovv.jetpreter.web;

import com.borunovv.jetpreter.web.server.InterpreterWSServer;
import com.borunovv.jetpreter.web.server.MessageHandler;

/**
 * @author borunovv
 */
public class Main {
    public static void main(String[] args) {
        new InterpreterWSServer(
                new MessageHandler())
                .execute(args);
    }
}
