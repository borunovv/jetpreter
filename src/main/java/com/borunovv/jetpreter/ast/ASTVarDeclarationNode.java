package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTVarDeclarationNode extends ASTNode {
    public ASTVarDeclarationNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }
}
