/*
 *
 */
package lk.ac.mrt.cse.padss.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.checks.SubscriptionBaseVisitor;
import org.sonar.java.model.expression.AssignmentExpressionTreeImpl;
import org.sonar.java.model.expression.IdentifierTreeImpl;
import org.sonar.java.model.expression.MemberSelectExpressionTreeImpl;
import org.sonar.java.model.expression.MethodInvocationTreeImpl;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.squidbridge.annotations.ActivatedByDefault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Rule(
  key = "padssSecurePanDataObject",
  name = "Secure Objects should discard at the end",
  tags = {"padss"},
  priority = Priority.CRITICAL)
@ActivatedByDefault

public class VariableEndCheck extends SubscriptionBaseVisitor {

  private static final String DEFAULT_CLASS_NAME = "padssDataObject";
  private static final String DEFAULT_METHOD_NAME = "discard";


  private List<String> secureObjects = new ArrayList<>();
  private Map<String, IdentifierTreeImpl> lastUsed = new HashMap<>();

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

        if(expression.is(Kind.ASSIGNMENT)){
          IdentifierTreeImpl assign= (IdentifierTreeImpl) ((AssignmentExpressionTreeImpl) expression).variable();
          if(DEFAULT_CLASS_NAME.equals(assign.symbolType().name())){
            addSecureObject(assign);
          }
        }else {
          ExpressionTree expressionClass = ((MemberSelectExpressionTreeImpl) ((MethodInvocationTreeImpl) expression).methodSelect()).expression();
          if(DEFAULT_CLASS_NAME.equals(expressionClass.symbolType().name())){
              String methodName = ((MemberSelectExpressionTreeImpl) ((MethodInvocationTreeImpl) expression).methodSelect()).identifier().name();
            if(DEFAULT_METHOD_NAME.equals(methodName)){
              removeSecureObject((IdentifierTreeImpl) expressionClass);
            }else {
                updateLastUsed((IdentifierTreeImpl) expressionClass);
            }
          }
        }
      }
      if (tree.is(Kind.COMPILATION_UNIT)) {
        this.checkNonCompliance();

      }

    super.leaveNode(tree);
  }

  private void addSecureObject(IdentifierTreeImpl tree){
      secureObjects.add(tree.symbol().name());
      lastUsed.put(tree.symbol().name(), tree);
  }

  private void removeSecureObject(IdentifierTreeImpl tree){
      secureObjects.remove(tree.symbol().name());
      lastUsed.remove(tree.symbol().name());
  }

  private void checkNonCompliance(){
        for (String objectName : secureObjects){
            addIssue(lastUsed.get(objectName),"Discard "+objectName+" object at the end");
        }
  }

    private void updateLastUsed(IdentifierTreeImpl tree){
        lastUsed.replace(tree.symbol().name(), tree);
    }



}
