package com.borunovv.jetpreter.interpreter.types;

import com.borunovv.jetpreter.ast.ASTLambdaNode;
import com.borunovv.jetpreter.core.util.CollectionUtils;

/**
 * Immutable.
 * Value for type 'LAMBDA'. Represents the lambda function.
 * Yes, it is first-class citizen ;-)
 *
 * @author borunovv
 */
public class ValueLambda extends Value {
    /**
     * Corresponding AST node for the lambda.
     * Used to call lambda from functions like map/reduce.
     */
    private final ASTLambdaNode lambdaNode;

    /**
     * C-tor
     * @param lambdaNode Corresponding AST node for the lambda.
     *                   Used to call lambda from functions like map/reduce.
     */
    public ValueLambda(ASTLambdaNode lambdaNode) {
        super(ValueTypes.LAMBDA);
        this.lambdaNode = lambdaNode;
    }

    /**
     * Return Corresponding AST node for the lambda.
     * Used to call lambda from functions like map/reduce.
     */
    public ASTLambdaNode getLambdaNode() {
        return lambdaNode;
    }

    @Override
    public String toString() {
        return "Lambda(" + CollectionUtils.toCommaSeparatedList(lambdaNode.getParamNames()) + ")";
    }
}
