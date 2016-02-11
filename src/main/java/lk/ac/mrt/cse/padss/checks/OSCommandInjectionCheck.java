
package lk.ac.mrt.cse.padss.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.checks.methods.MethodMatcher;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.NewArrayTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import org.sonar.java.checks.AbstractInjectionChecker;

import java.util.List;

@Rule(
  key = "S2076",
  name = "Values passed to OS commands should be sanitized",
  priority = Priority.CRITICAL,
  tags = {"pa-dss"})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.INPUT_VALIDATION_AND_REPRESENTATION)
@SqaleConstantRemediation("30min")
public class OSCommandInjectionCheck extends AbstractInjectionChecker {

  private static final MethodMatcher RUNTIME_EXEC_MATCHER = MethodMatcher.create()
    .typeDefinition("java.lang.Runtime")
    .name("exec")
    .withNoParameterConstraint();

  private static final MethodMatcher PROCESS_BUILDER_COMMAND_MATCHER = MethodMatcher.create()
    .typeDefinition("java.lang.ProcessBuilder")
    .name("command")
    .withNoParameterConstraint();

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.METHOD_INVOCATION, Tree.Kind.NEW_CLASS);
  }

  @Override
  public void visitNode(Tree tree) {
    if (hasSemantic()) {
      if (tree.is(Tree.Kind.METHOD_INVOCATION)) {
        MethodInvocationTree mit = (MethodInvocationTree) tree;
        Arguments arguments = mit.arguments();
        if (RUNTIME_EXEC_MATCHER.matches(mit)) {
          checkForIssue(tree, arguments.get(0));
        } else if (PROCESS_BUILDER_COMMAND_MATCHER.matches(mit) && !arguments.isEmpty()) {
          checkForIssue(tree, arguments);
        }
      } else if (((NewClassTree) tree).symbolType().is("java.lang.ProcessBuilder")) {
        checkForIssue(tree, ((NewClassTree) tree).arguments());
      }
    }
  }

  private void checkForIssue(Tree tree, Arguments arguments) {
    for (ExpressionTree arg : arguments) {
      checkForIssue(tree, arg);
    }
  }

  private void checkForIssue(Tree tree, ExpressionTree arg) {
    if (isDynamicArray(arg, tree) && !arg.symbolType().isSubtypeOf("java.util.List")) {
      addIssue(arg, "Make sure \"" + parameterName + "\" is properly sanitized before use in this OS command.");
    }
  }

  private boolean isDynamicArray(ExpressionTree arg, Tree mit) {
    if (arg.is(Tree.Kind.NEW_ARRAY)) {
      NewArrayTree nat = (NewArrayTree) arg;
      for (ExpressionTree expressionTree : nat.initializers()) {
        if (isDynamicString(mit, expressionTree, null)) {
          return true;
        }
      }
      return false;
    }

    setParameterNameFromArgument(arg);
    boolean argIsString = arg.symbolType().is("java.lang.String");
    return !argIsString || isDynamicString(mit, arg, null);
  }
}