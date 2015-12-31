/*
 *
 */
package lk.ac.mrt.cse.padss.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.squidbridge.annotations.ActivatedByDefault;

import javax.annotation.Nullable;
import java.util.List;

@Rule(
        key = "SecureObjectShouldNotConvertToString",
        name = "Secure Objects should not convert toString",
        description = "This rule will check whether secure object convert to string",
        tags = {"pa-dss"})
@ActivatedByDefault
public class SecureObjectMethodsCheck extends IssuableSubscriptionVisitor {

    private static final String DEFAULT_CLASS_NAME = "padssDataObject";

    @RuleProperty(
            defaultValue = DEFAULT_CLASS_NAME,
            description = "Name of the class which use to save secure data")
    protected String className;

    @Override
    public List<Kind> nodesToVisit() {
        return ImmutableList.of(Kind.CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        ClassTree classTree = (ClassTree) tree;
        if (className.equals(classTree.simpleName().name())) {
            List<Tree> membersList = classTree.members();
            for (Tree t : membersList) {
                if (t.is(Kind.METHOD)) {
                    MethodTree methodTree = (MethodTree) t;

                    if (hasPANDataReturnValuve(methodTree)) {
                        String message = "you cannot return variable value from secure object";
                        this.addIssue(methodTree.block().body().get(methodTree.block().body().size() - 1), message);
                    }
                    TypeTree temp;
                    temp = methodTree.returnType();
                    //if(temp.is())
                    System.out.println();
                }
            }
        }
        System.out.println();
        super.visitNode(tree);
    }

    protected boolean hasPANDataReturnValuve(MethodTree methodTree) {
        if (methodTree.block().body().size() > 0) {
            if (methodTree.block().body().get(methodTree.block().body().size() - 1).is(Kind.RETURN_STATEMENT)) {

                ReturnStatementTree returnTree = (ReturnStatementTree) methodTree.block().body().get(methodTree.block().body().size() - 1);
                if (returnTree.expression().is(Kind.STRING_LITERAL)) {
                    return false;
                }
                if (returnTree.expression().is(Kind.IDENTIFIER)) {

                    return isDynamicVariable((IdentifierTree) returnTree.expression());
                }

                if (returnTree.expression().is(Kind.PLUS)) {

                    return isDynamicString((BinaryExpressionTree) returnTree.expression());
                }


            }
            return false;
        } else {
            return false;
        }
    }

    protected boolean isDynamicVariable(IdentifierTree identifierTree) {
        VariableTree variableTree = (VariableTree) identifierTree.symbol().declaration();
        if (variableTree.initializer() != null) {
            return false;
        }
        return true;
    }

    protected boolean isDynamicString(BinaryExpressionTree binaryExpressionTree) {
        boolean isLeftDynamic = false;
        boolean isRightDynamic = false;
        if (binaryExpressionTree.leftOperand().is(Kind.IDENTIFIER)) {
            isLeftDynamic = isDynamicVariable((IdentifierTree) binaryExpressionTree.leftOperand());
        }
        if (binaryExpressionTree.rightOperand().is(Kind.IDENTIFIER)) {
            isRightDynamic = isDynamicVariable((IdentifierTree) binaryExpressionTree.rightOperand());
        }

        if (isLeftDynamic || isRightDynamic) {
            return true;
        }

        return false;
    }


}
