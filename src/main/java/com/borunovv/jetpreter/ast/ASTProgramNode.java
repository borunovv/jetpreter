package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTProgramNode extends ASTNode {
    public ASTProgramNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }
}
