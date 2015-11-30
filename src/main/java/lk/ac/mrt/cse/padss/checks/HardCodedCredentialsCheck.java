package lk.ac.mrt.cse.padss.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Rule;
import org.sonar.java.checks.SubscriptionBaseVisitor;
import org.sonar.java.model.LiteralUtils;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;

@Rule(
        key = "Hard-codedPasswords",
        name = "Passwords should not be hard-coded",
        description = "This rule detects the presence of hard coded passwords in the source",
        tags = {"PA-DSS"})

public class HardCodedCredentialsCheck extends SubscriptionBaseVisitor {

    private static final Pattern PASSWORD_LITERAL_PATTERN = Pattern.compile("(password|passwd|pwd)=..", Pattern.CASE_INSENSITIVE);
    private static final Pattern PASSWORD_VARIABLE_PATTERN = Pattern.compile("(password|passwd|pwd)", Pattern.CASE_INSENSITIVE);

    private static boolean isSettingPassword(MethodInvocationTree tree) {
        List<ExpressionTree> arguments = tree.arguments();
        return arguments.size() == 2 && argumentsAreLiterals(arguments) && isPassword((LiteralTree) arguments.get(0));
    }

    private static boolean isPassword(LiteralTree argument) {
        return argument.is(Tree.Kind.STRING_LITERAL) && PASSWORD_VARIABLE_PATTERN.matcher(LiteralUtils.trimQuotes(argument.value())).matches();
    }

    private static boolean argumentsAreLiterals(List<ExpressionTree> arguments) {
        for (ExpressionTree argument : arguments) {
            if (!argument.is(
                    Kind.INT_LITERAL,
                    Kind.LONG_LITERAL,
                    Kind.FLOAT_LITERAL,
                    Kind.DOUBLE_LITERAL,
                    Kind.BOOLEAN_LITERAL,
                    Kind.CHAR_LITERAL,
                    Kind.STRING_LITERAL,
                    Kind.NULL_LITERAL)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isStringLiteral(@Nullable ExpressionTree initializer) {
        return initializer != null && initializer.is(Tree.Kind.STRING_LITERAL);
    }

    private static boolean isPasswordVariableName(IdentifierTree identifierTree) {
        return PASSWORD_VARIABLE_PATTERN.matcher(identifierTree.name()).find();
    }

    private static boolean isPasswordVariable(ExpressionTree variable) {
        if (variable.is(Tree.Kind.MEMBER_SELECT)) {
            return isPasswordVariableName(((MemberSelectExpressionTree) variable).identifier());
        } else if (variable.is(Tree.Kind.IDENTIFIER)) {
            return isPasswordVariableName((IdentifierTree) variable);
        }
        return false;
    }

    @Override
    public List<Kind> nodesToVisit() {
        return ImmutableList.of(Tree.Kind.STRING_LITERAL, Tree.Kind.VARIABLE, Tree.Kind.ASSIGNMENT, Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.STRING_LITERAL)) {
            String literalValue = ((LiteralTree) tree).value();
            if (PASSWORD_LITERAL_PATTERN.matcher(literalValue).find()) {
                reportIssue(tree);
            }
        } else if (tree.is(Tree.Kind.VARIABLE)) {
            VariableTree variable = (VariableTree) tree;
            IdentifierTree simpleName = variable.simpleName();
            if (isStringLiteral(variable.initializer()) && isPasswordVariableName(simpleName)) {
                reportIssue(simpleName);
            }
        } else if (tree.is(Tree.Kind.ASSIGNMENT)) {
            AssignmentExpressionTree assignmentExpression = (AssignmentExpressionTree) tree;
            ExpressionTree variable = assignmentExpression.variable();
            if (isStringLiteral(assignmentExpression.expression()) && isPasswordVariable(variable)) {
                reportIssue(variable);
            }
        } else {
            MethodInvocationTree mit = (MethodInvocationTree) tree;
            if (isSettingPassword(mit)) {
                reportIssue((mit).methodSelect());
            }
        }
    }

    private void reportIssue(Tree tree) {
        addIssue(tree, "Remove this hard-coded password.");
    }

}

