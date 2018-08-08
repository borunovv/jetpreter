package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTLineNode extends ASTNode {
    public ASTLineNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }
}
