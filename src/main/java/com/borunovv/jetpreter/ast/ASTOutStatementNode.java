package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTOutStatementNode extends ASTNode {
    public ASTOutStatementNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }
}
