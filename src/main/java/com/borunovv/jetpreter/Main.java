package com.borunovv.jetpreter;

import com.borunovv.jetpreter.javacc.generated.Program;

import java.io.StringReader;

public class Main {

    private static String PROGRAM = "var a = 5\n";

    public static void main(String[] args) throws Exception {
        Program parser = new Program(new StringReader("var"));
        parser.var();
    }
}