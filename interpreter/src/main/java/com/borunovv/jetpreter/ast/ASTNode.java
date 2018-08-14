package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;
import com.borunovv.jetpreter.javacc.generated.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all AST nodes.
 *
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

    /**
     * Write AST as string.
     *
     * @param writer the {@link StringBuilder} to write into.
     */
    public void dump(StringBuilder writer) {
       dump(writer, 0);
    }

    /**
     * Write AST as string.
     * Used for recursive calls.
     *
     * @param writer the {@link StringBuilder} to write into
     * @param level the current depth level in tree
     */
    protected void dump(StringBuilder writer, int level) {
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

    /**
     * Return first token for this node.
     */
    protected Token getFirstToken() {
        return wrappedNode.jjtGetFirstToken();
    }

    /**
     * Return last token for this node.
     */
    protected Token getLastToken() {
        return wrappedNode.jjtGetLastToken();
    }

    /**
     * Used to exclude redundant nodes from AST.
     * For example consider simplest expression: '5' (just integer).
     * Here the AST can look like: 'Expression->..->MulOperation->..->Factor->IntNumber'.
     * This AST can be compacted to just 'IntNumber' since it is special case and we have no operations like '5*6' etc.
     *
     * @return AST without redundant nodes.
     */
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
