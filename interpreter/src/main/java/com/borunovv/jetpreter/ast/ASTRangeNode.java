package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.core.contract.Precondition;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.types.*;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

import java.util.ArrayList;
import java.util.List;

/**
 * AST tree node for Range() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTRangeNode extends ASTNode {
    public ASTRangeNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    private static final long MAX_RANGE_SIZE = 1000000;

    /**
     * Interpret the current node. Put result onto the stack.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        ASTNode first = children.get(0);
        ASTNode second = children.get(1);

        first.interpret(ctx);
        Value firstValue = ctx.pop();

        second.interpret(ctx);
        Value secondValue = ctx.pop();

        Precondition.expected(firstValue instanceof ValueLong,
                "Range's left bound must be of type " + ValueTypes.INT + "."
                        + " Actual type is " + firstValue.getTypeName() + ".");

        Precondition.expected(secondValue instanceof ValueLong,
                "Range's right bound must be of type " + ValueTypes.INT + "."
                        + " Actual type is " + secondValue.getTypeName() + ".");

        long leftBound = ((ValueLong) firstValue).getValue();
        long rightBound = ((ValueLong) secondValue).getValue();
        long count = rightBound - leftBound + 1;

        Precondition.expected(count > 0, "Bad range: {" + leftBound + ", " + rightBound + "}. Left bound > Right bound.");
        Precondition.expected(count <= MAX_RANGE_SIZE,
                "Too big range: {" + leftBound + ", " + rightBound
                        + "}. Size must not exceed " + MAX_RANGE_SIZE
                        + ". Actual size: " + count + ".");

        ctx.push(new ValueList(generateList(leftBound, rightBound)));
    }

    /**
     * Generate list of integer numbers from interval [from, to] (inclusive both bounds).
     *
     * @param from left bound (inclusive)
     * @param to   right bound (inclusive)
     * @return list of integer numbers from interval [from, to] (inclusive both bounds).
     */
    private List<ValueScalar> generateList(long from, long to) {
        List<ValueScalar> result = new ArrayList<>((int) (to - from + 1));
        for (long item = from; item <= to; ++item) {
            result.add(new ValueLong(item));
        }
        return result;
    }
}
