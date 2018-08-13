package com.borunovv.jetpreter.interpreter.types;

import com.borunovv.jetpreter.ast.ASTLambdaNode;
import com.borunovv.jetpreter.core.util.CollectionUtils;

/**
 * @author borunovv
 */
public class ValueLambda extends Value {
    private final ASTLambdaNode lambdaNode;

    public ValueLambda(ASTLambdaNode lambdaNode) {
        super(ValueTypes.LAMBDA);
        this.lambdaNode = lambdaNode;
    }

    public ASTLambdaNode getLambdaNode() {
        return lambdaNode;
    }

    @Override
    public String toString() {
        return "Lambda(" + CollectionUtils.toCommaSeparatedList(lambdaNode.getParamNames()) + ")";
    }
}
