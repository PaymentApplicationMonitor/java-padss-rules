package lk.ac.mrt.cse.padss.checks;


import org.sonar.check.Rule;
import org.sonar.java.checks.methods.MethodInvocationMatcherCollection;
import org.sonar.java.checks.methods.MethodMatcher;
import org.sonar.java.checks.methods.TypeCriteria;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(
        key = "SQLInjection",
        name = "Values passed to SQL commands should be sanitized",
        description = "This rule will check for any non-sanitized variables to be passed to database",
        tags = {"PA-DSS"})

public class SQLInjectionCheck extends AbstractInjectionChecker {

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
        return !methodTree.arguments().isEmpty() && (STATEMENT_EXECUTE_QUERY_MATCHER.matches(methodTree) || CONNECTION_MATCHERS.anyMatch(methodTree));
    }

    private static boolean isHibernateCall(MethodInvocationTree methodTree) {
        return HIBERNATE_SESSION_CREATE_QUERY_MATCHER.matches(methodTree);
    }

    private static boolean isEntityManagerCreateNativeQuery(MethodInvocationTree methodTree) {
        return ENTITY_MANAGER_CREATE_NATIVE_QUERY_MATCHER.matches(methodTree);
    }

    @Override
    public void visitNode(Tree tree) {
        MethodInvocationTree methodTree = (MethodInvocationTree) tree;
        boolean isHibernateCall = isHibernateCall(methodTree);
        if (isHibernateCall || isExecuteQueryOrPrepareStatement(methodTree) || isEntityManagerCreateNativeQuery(methodTree)) {
            //We want to check the argument for the three methods.
            ExpressionTree arg = methodTree.arguments().get(0);
            parameterName = "";
            if (isDynamicString(methodTree, arg, null, true)) {
                String message = "\"" + parameterName + "\" is provided externally to the method and not sanitized before use.";
                if (isHibernateCall) {
                    message = "Use Hibernate's parameter binding instead of concatenation.";
                }
                addIssue(methodTree, message);
            }
        }
    }
}
