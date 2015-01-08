/*
 *
 */
package lk.ac.mrt.cse.padss.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.java.checks.methods.MethodInvocationMatcherCollection;
import org.sonar.java.checks.methods.MethodMatcher;
import org.sonar.java.checks.methods.TypeCriteria;
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
public class SQLMethodsInSecureObjectCheck extends IssuableSubscriptionVisitor {

    private static final String DEFAULT_CLASS_NAME = "padssDataObject";

    @RuleProperty(
            defaultValue = DEFAULT_CLASS_NAME,
            description = "Name of the class which use to save secure data")
    protected String className;

    private static final MethodMatcher HIBERNATE_SESSION_CREATE_QUERY_MATCHER = MethodMatcher.create()
            // method from the interface org.hibernate.SharedSessionContract, implemented by org.hibernate.Session
            .callSite(TypeCriteria.subtypeOf("org.hibernate.Session"))
            .name("createQuery")
            .withNoParameterConstraint();

    private static final MethodMatcher STATEMENT_EXECUTE_QUERY_MATCHER = MethodMatcher.create()
            .typeDefinition(TypeCriteria.subtypeOf("java.sql.Statement"))
            .name("executeQuery")
            .withNoParameterConstraint();

    private static final MethodInvocationMatcherCollection CONNECTION_MATCHERS = MethodInvocationMatcherCollection.create(
            MethodMatcher.create().typeDefinition(TypeCriteria.subtypeOf("java.sql.Connection")).name("prepareStatement").withNoParameterConstraint(),
            MethodMatcher.create().typeDefinition(TypeCriteria.subtypeOf("java.sql.Connection")).name("prepareCall").withNoParameterConstraint());

    private static final MethodMatcher ENTITY_MANAGER_CREATE_NATIVE_QUERY_MATCHER = MethodMatcher.create()
            .typeDefinition("javax.persistence.EntityManager")
            .name("createNativeQuery")
            .withNoParameterConstraint();

    private static boolean isExecuteQueryOrPrepareStatement(MethodInvocationTree methodTree) {
        return (STATEMENT_EXECUTE_QUERY_MATCHER.matches(methodTree) || CONNECTION_MATCHERS.anyMatch(methodTree));
    }

    private static boolean isHibernateCall(MethodInvocationTree methodTree) {
        return HIBERNATE_SESSION_CREATE_QUERY_MATCHER.matches(methodTree);
    }

    private static boolean isEntityManagerCreateNativeQuery(MethodInvocationTree methodTree) {
        return ENTITY_MANAGER_CREATE_NATIVE_QUERY_MATCHER.matches(methodTree);
    }

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
                    List<StatementTree> methodBodyList = methodTree.block().body();
                    for (StatementTree st : methodBodyList) {
                        boolean isMethodInvocationFoun = false;
                        MethodInvocationTree methodInvocationTree = null;
                        if (st.is(Kind.VARIABLE)) {
                            if (((VariableTree) st).initializer() != null) {
                                if (((VariableTree) st).initializer().is(Kind.METHOD_INVOCATION)) {
                                    isMethodInvocationFoun = true;
                                    methodInvocationTree = (MethodInvocationTree) ((VariableTree) st).initializer();
                                }
                            }
                        }
                        if (st.is(Kind.EXPRESSION_STATEMENT)) {
                            if (((ExpressionStatementTree) st).expression() != null) {
                                if (((ExpressionStatementTree) st).expression().is(Kind.ASSIGNMENT)) {
                                    AssignmentExpressionTree asTree = (AssignmentExpressionTree) ((ExpressionStatementTree) st).expression();
                                    if (asTree.expression().is(Kind.METHOD_INVOCATION)) {
                                        isMethodInvocationFoun = true;
                                        methodInvocationTree = (MethodInvocationTree) asTree.expression();
                                    }
                                }
                            }
                        }
                        if (isMethodInvocationFoun) {
                            boolean isHibernateCall = isHibernateCall(methodInvocationTree);
                            if (isHibernateCall || isExecuteQueryOrPrepareStatement(methodInvocationTree) || isEntityManagerCreateNativeQuery(methodInvocationTree)) {
                                addIssue(methodInvocationTree, "SQL methods not allowed in secure Object");
                            }
                            if (isEntityManagerCreateNativeQuery(methodInvocationTree)){
                                System.out.println();
                            }
                            if (isExecuteQueryOrPrepareStatement(methodInvocationTree)){
                                System.out.println();
                            }
                            System.out.println();
                        }

                    }
                    System.out.println();
                }
            }
        }
        super.visitNode(tree);
    }


}
