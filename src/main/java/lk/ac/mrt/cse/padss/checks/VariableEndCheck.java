/*
 *
 */
package lk.ac.mrt.cse.padss.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
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

  private static final String DEFAULT_CLASS_NAME = "padssDataObjec";
  private static final String DEFAULT_METHOD_NAME = "discard";


  private List<String> secureObjects = new ArrayList<>();
  private Map<String, IdentifierTreeImpl> lastUsed = new HashMap<>();

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

        if(expression.is(Kind.ASSIGNMENT)){
          IdentifierTreeImpl assign= (IdentifierTreeImpl) ((AssignmentExpressionTreeImpl) expression).variable();
          if(className.equals(assign.symbolType().name())){
            addSecureObject(assign);
          }
        }else {
          ExpressionTree expressionClass = ((MemberSelectExpressionTreeImpl) ((MethodInvocationTreeImpl) expression).methodSelect()).expression();
          if(className.equals(expressionClass.symbolType().name())){
              String methodName = ((MemberSelectExpressionTreeImpl) ((MethodInvocationTreeImpl) expression).methodSelect()).identifier().name();
            if(discardMethodName.equals(methodName)){
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
