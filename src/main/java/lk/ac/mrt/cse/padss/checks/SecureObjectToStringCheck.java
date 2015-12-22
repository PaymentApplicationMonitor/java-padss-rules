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

import java.util.List;

@Rule(
        key = "SecureObjectShouldNotConvertToString",
        name = "Secure Objects should not convert toString",
        description = "This rule will check whether secure object convert to string",
        tags = {"pa-dss"})
@ActivatedByDefault
public class SecureObjectToStringCheck extends IssuableSubscriptionVisitor {

    private static final String DEFAULT_CLASS_NAME = "padssDataObject";
    private static final String DEFAULT_METHOD_NAME = "toString";

    @RuleProperty(
            defaultValue = DEFAULT_CLASS_NAME,
            description = "Name of the class which use to save secure data")
    protected String className;

    @RuleProperty(
            defaultValue = DEFAULT_METHOD_NAME,
            description = "Name of the method implement to discard data")
    protected String disallowedMethodName;

    @Override
    public List<Kind> nodesToVisit() {
        return ImmutableList.of(Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        MethodInvocationTree methodTree = (MethodInvocationTree) tree;

        if (((MethodInvocationTree) tree).methodSelect().is(Kind.MEMBER_SELECT)) {
            ExpressionTree expressionClass = ((MemberSelectExpressionTree) ((MethodInvocationTree) tree).methodSelect()).expression();
            if (className.equals(expressionClass.symbolType().name())) {
                String methodName = ((MemberSelectExpressionTree) ((MethodInvocationTree) tree).methodSelect()).identifier().name();
                //
                if (disallowedMethodName.equals(methodName)) {
                    this.addIssue(methodTree,"toString Method not allowed for secure object");
                }
            }
        }
        super.visitNode(tree);
    }


}
