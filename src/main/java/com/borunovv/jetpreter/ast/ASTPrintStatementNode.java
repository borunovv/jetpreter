package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTPrintStatementNode extends ASTNode {
    public ASTPrintStatementNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }
}
