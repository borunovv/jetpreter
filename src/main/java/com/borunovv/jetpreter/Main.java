package com.borunovv.jetpreter;

import com.borunovv.jetpreter.ast.ASTNode;
import com.borunovv.jetpreter.ast.ASTNodeFactory;
import com.borunovv.jetpreter.javacc.generated.Node;
import com.borunovv.jetpreter.javacc.generated.ProgramParser;

import java.io.StringReader;

public class Main {

    private static String PROGRAM = "" +
            "var a = -5.0\n" +
            "a = 7.21\n" +
            "print \"ABC D\"\n" +
            "out 123\n";

    public static void main(String[] args) throws Exception {
        ProgramParser parser = new ProgramParser(new StringReader(PROGRAM));
        parser.Program();
        Node root = parser.rootNode();

        ASTNode astRoot = ASTNodeFactory.buildTree(root);

        StringBuilder sb = new StringBuilder();
        astRoot.dump(sb, 0);
        System.out.println(sb.toString());
    }
}