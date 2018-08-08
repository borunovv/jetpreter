package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.javacc.generated.SimpleNode;

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

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
