package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.javacc.generated.Node;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

import static com.borunovv.jetpreter.javacc.generated.ProgramParserTreeConstants.*;

/**
 * @author borunovv
 */
public class ASTNodeFactory {

    public static ASTNode buildTree(Node node) {
        ASTNode astNode = create(node);
        for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
            // depth-first recursion
            astNode.addChild(buildTree(node.jjtGetChild(i)));
        }
        return astNode;
    }

    private static ASTNode create(Node node) {
        SimpleNode simpleNode = (SimpleNode) node;
        switch (simpleNode.getId()) {
            case JJTPROGRAM:
                return new ASTProgramNode(simpleNode);
            case JJTLINE:
                return new ASTLineNode(simpleNode);
            case JJTVARDECLARATION:
                return new ASTVarDeclarationNode(simpleNode);
            case JJTVARASSIGNMENT:
                return new ASTVarAssignmentNode(simpleNode);
            case JJTPRINTSTATEMENT:
                return new ASTPrintStatementNode(simpleNode);
            case JJTOUTSTATEMENT:
                return new ASTOutStatementNode(simpleNode);
            case JJTINTNUMBER:
                return new ASTIntNumberNode(simpleNode);
            case JJTFLOATNUMBER:
                return new ASTFloatNumberNode(simpleNode);
            case JJTADDOPERATION:
                return new ASTAddOperationNode(simpleNode);
            case JJTMULOPERATION:
                return new ASTMulOperationNode(simpleNode);
            case JJTPOWEROPERATION:
                return new ASTPowerOperationNode(simpleNode);
            case JJTADDOPERATOR :
                return new ASTAddOperatorNode(simpleNode);
            case JJTMULOPERATOR :
                return new ASTMulOperatorNode(simpleNode);
            case JJTPOWEROPERATOR :
                return new ASTPowerOperatorNode(simpleNode);
            case JJTVARID :
                return new ASTVarIdNode(simpleNode);

            default:
                throw new IllegalArgumentException("Unimplemented node type: " + simpleNode.getId());
        }
    }

}
