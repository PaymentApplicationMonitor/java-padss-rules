/*
 *
 */
package lk.ac.mrt.cse.padss.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.squidbridge.annotations.ActivatedByDefault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Rule(
        key = "SecureDataDiscarding",
        name = "Secure Objects should discard at the end",
        description = "This rule will check whether data is discarded properly before garbage collection",
        tags = {"pa-dss"})
@ActivatedByDefault
public class SecureObjectToStringCheck extends IssuableSubscriptionVisitor {

    private static final String DEFAULT_CLASS_NAME = "padssDataObject";
    private static final String DEFAULT_METHOD_NAME = "discard";

    private List<String> secureObjects = new ArrayList<>();
    private Map<String, IdentifierTree> lastUsed = new HashMap<>();

    @RuleProperty(
            defaultValue = DEFAULT_CLASS_NAME,
            description = "Name of the class which use to save secure data")
    protected String className;

    @RuleProperty(
            defaultValue = DEFAULT_METHOD_NAME,
            description = "Name of the method implement to discard data")
    protected String discardMethodName;

    @Override
    public List<Kind> nodesToVisit() {
        return ImmutableList.of(
                Kind.EXPRESSION_STATEMENT, Kind.COMPILATION_UNIT);
    }

    @Override
    public void leaveNode(Tree tree) {
        if (tree.is(Kind.EXPRESSION_STATEMENT)) {
            ExpressionStatementTree expressionStatement = (ExpressionStatementTree) tree;
            ExpressionTree expression = expressionStatement.expression();
            if (expression.is(Kind.ASSIGNMENT)) {
                if (((AssignmentExpressionTree) expression).variable().is(Kind.IDENTIFIER)) {
                    IdentifierTree assign = (IdentifierTree) ((AssignmentExpressionTree) expression).variable();
                    if (className.equals(assign.symbolType().name())) {
                        addSecureObject(assign);
                    }
                }
            } else if (expression.is(Kind.METHOD_INVOCATION)) {
                if (((MethodInvocationTree) expression).methodSelect().is(Kind.MEMBER_SELECT)) {
                    ExpressionTree expressionClass = ((MemberSelectExpressionTree) ((MethodInvocationTree) expression).methodSelect()).expression();
                    if (className.equals(expressionClass.symbolType().name())) {
                        String methodName = ((MemberSelectExpressionTree) ((MethodInvocationTree) expression).methodSelect()).identifier().name();
                        if (discardMethodName.equals(methodName)) {
                            removeSecureObject((IdentifierTree) expressionClass);
                        } else {
                            updateLastUsed((IdentifierTree) expressionClass);
                        }
                    }
                }
            }
        }
        if (tree.is(Kind.COMPILATION_UNIT)) {
            this.checkNonCompliance();
        }
        super.leaveNode(tree);
    }

    private void addSecureObject(IdentifierTree tree) {
        secureObjects.add(tree.symbol().name());
        lastUsed.put(tree.symbol().name(), tree);
    }

    private void removeSecureObject(IdentifierTree tree) {
        secureObjects.remove(tree.symbol().name());
        lastUsed.remove(tree.symbol().name());
    }

    private void checkNonCompliance() {
        for (String objectName : secureObjects) {
            addIssue(lastUsed.get(objectName), "Discard " + objectName + " object at the end");
        }
    }

    private void updateLastUsed(IdentifierTree tree) {
        lastUsed.replace(tree.symbol().name(), tree);
    }


}
