package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;
import com.borunovv.jetpreter.javacc.generated.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * @author borunovv
 */
public abstract class ASTNode {
    protected SimpleNode wrappedNode;
    protected List<ASTNode> children = new ArrayList<>();

    public ASTNode(SimpleNode wrappedNode) {
        this.wrappedNode = wrappedNode;
    }

    public void addChild(ASTNode child) {
        children.add(child);
    }

    public abstract void interpret(Context ctx);

    public void dump(StringBuilder writer) {
       dump(writer, 0);
    }

    public void dump(StringBuilder writer, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; ++i) {
            sb.append(" ");
        }
        String prefix = sb.toString();
        writer.append(prefix).append(toString()).append("\n");

        for (ASTNode child : children) {
            child.dump(writer, level + 1);
        }
    }

    protected Token getFirstToken() {
        return wrappedNode.jjtGetFirstToken();
    }

    protected Token getLastToken() {
        return wrappedNode.jjtGetLastToken();
    }

    protected ASTNode compact() {
        for (int i = 0; i < children.size(); i++) {
            children.set(i, children.get(i).compact()); // recursion
        }
        return this;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
