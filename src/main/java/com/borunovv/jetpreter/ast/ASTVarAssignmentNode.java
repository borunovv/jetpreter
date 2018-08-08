package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTVarAssignmentNode extends ASTNode {
    public ASTVarAssignmentNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }
}
