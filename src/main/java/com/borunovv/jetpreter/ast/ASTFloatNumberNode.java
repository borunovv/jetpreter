package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTFloatNumberNode extends ASTNode {
    public ASTFloatNumberNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }
}
