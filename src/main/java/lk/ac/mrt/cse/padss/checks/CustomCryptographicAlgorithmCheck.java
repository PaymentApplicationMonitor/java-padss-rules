
package lk.ac.mrt.cse.padss.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import org.sonar.java.checks.AbstractInjectionChecker;

import java.util.List;

@Rule(
  key = "S2257",
  name = "Only standard cryptographic algorithms should be used",
  tags = {"pa-dss"})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.SECURITY_FEATURES)
@SqaleConstantRemediation("1d")
public class CustomCryptographicAlgorithmCheck extends AbstractInjectionChecker {

  private static final String MESSAGE_DIGEST_QUALIFIED_NAME = "java.security.MessageDigest";

  @Override
  public List<Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.CLASS);
  }

  @Override
  public void visitNode(Tree tree) {
    if (hasSemantic() && isJavaSecurityMessageDigestSubClass((ClassTree) tree)) {
      addIssue(tree, "Use a standard algorithm instead of creating a custom one.");
    }
  }

  private static boolean isJavaSecurityMessageDigestSubClass(ClassTree tree) {
    Symbol.TypeSymbol classSymbol = tree.symbol();
    // Corner case: A type is a subtype of itself
    return classSymbol != null && !classSymbol.type().is(MESSAGE_DIGEST_QUALIFIED_NAME) &&
      classSymbol.type().isSubtypeOf(MESSAGE_DIGEST_QUALIFIED_NAME);
  }
}
