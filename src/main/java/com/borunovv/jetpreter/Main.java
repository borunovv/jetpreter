package com.borunovv.jetpreter;

import com.borunovv.jetpreter.ast.ASTNode;
import com.borunovv.jetpreter.ast.ASTNodeFactory;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.Interpreter;
import com.borunovv.jetpreter.javacc.generated.Node;
import com.borunovv.jetpreter.javacc.generated.ProgramParser;

import java.io.StringReader;

public class Main {

    private static String PROGRAM = "" +
            "print \"Hello, world!\\n\" \n" +
            "var x = 5 + 6 +.1 - 1.5 * 3 / 2 ^ -2.1 \n" +
            "var y = 3^2 + 1 \n" +
            "print \"result=\" \n" +
            "out x * x ^ 2 * y\n";

    public static void main(String[] args) throws Exception {
        Interpreter interpreter = new Interpreter(System.out::print);
        interpreter.verify(PROGRAM);

        ProgramParser parser = new ProgramParser(new StringReader(PROGRAM));
        parser.Program();
        Node root = parser.rootNode();

        ASTNode astRoot = ASTNodeFactory.buildTree(root);

        StringBuilder sb = new StringBuilder();
        astRoot.dump(sb, 0);
        System.out.println(sb.toString());

        Context ctx = new Context(System.out::print);
        System.out.println("\nOutput:");
        astRoot.interpret(ctx);
    }
}