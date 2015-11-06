package lk.ac.mrt.cse.padss.checks;

import java.util.List;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.google.common.collect.ImmutableList;
import org.sonar.java.checks.methods.MethodMatcher;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.java.checks.methods.AbstractMethodDetection;
import org.sonar.java.checks.methods.TypeCriteria;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.util.regex.Pattern;

@Rule(key = "AvoidAnnotation",
        name = "Avoid usage of annotation",
        description = "This rule detects usage of configured annotation",
        tags = {"example"})
public class HTTPCheck extends BaseTreeVisitor implements JavaFileScanner{

  private static final String DEFAULT_VALUE = "Inject";
  private static final String DEFAULT_FORMAT = "^(http?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
  private JavaFileScannerContext context;
    public String format = DEFAULT_FORMAT;
    private Pattern pattern = null;

  /**
   * Name of the annotation to avoid. Value can be set by users in Quality profiles.
   * The key
   */
  @RuleProperty(
          defaultValue = DEFAULT_VALUE,
          description = "Name of the annotation to avoid, without the prefix @, for instance 'Override'")
  protected String name;

  @Override
  public void scanFile(JavaFileScannerContext context) {
    this.context = context;
      if (pattern == null) {
          pattern = Pattern.compile(format, Pattern.DOTALL);
      }
      this.context = context;
      scan(context.getTree());

    System.out.println(PrinterVisitor.print(context.getTree()));
  }
    @Override
    public void visitLiteral(LiteralTree tree) {
        String temp= tree.value().substring(1,tree.value().length()-1);
        if (pattern.matcher(temp).matches()) {
            context.addIssue(tree, this,String.format("Avoid using non secure urls %s", temp));
        }

        super.visitLiteral(tree);
    }
//  @Override
//  public void visitMethod(MethodTree tree) {
//    List<AnnotationTree> annotations = tree.modifiers().annotations();
//    for (AnnotationTree annotationTree : annotations) {
//      if (annotationTree.annotationType().is(Tree.Kind.IDENTIFIER)) {
//        IdentifierTree idf = (IdentifierTree) annotationTree.annotationType();
//        System.out.println(idf.name());
//
//        if (idf.name().equals(name)) {
//          context.addIssue(idf, this, String.format("Avoid using annotation @%s", name));
//        }
//      }
//    }
//
//    // The call to the super implementation allows to continue the visit of the AST.
//    // Be careful to always call this method to visit every node of the tree.
//    super.visitMethod(tree);
//  }

}

