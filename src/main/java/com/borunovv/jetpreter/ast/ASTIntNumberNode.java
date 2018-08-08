package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTIntNumberNode extends ASTNode {
    public ASTIntNumberNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }
}
