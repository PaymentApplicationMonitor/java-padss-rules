
package lk.ac.mrt.cse.padss.checks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.checks.helpers.JavaPropertiesHelper;
import org.sonar.java.checks.methods.AbstractMethodDetection;
import org.sonar.java.checks.methods.MethodMatcher;
import org.sonar.java.checks.methods.TypeCriteria;
import org.sonar.java.model.LiteralUtils;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.java.checks.helpers.*;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.util.List;
import java.util.Map;

@Rule(
  key = "S2070",
  name = "SHA-1 and Message-Digest hash algorithms should not be used",
  priority = Priority.CRITICAL,
  tags = {"pa-dss"})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.SECURITY_FEATURES)
@SqaleConstantRemediation("30min")
public class DeprecatedHashAlgorithmCheck extends AbstractMethodDetection {

  private static final String JAVA_LANG_STRING = "java.lang.String";
  private static final String MD5 = "MD5";
  private static final String SHA1 = "SHA1";

  private static final Map<String, String> ALGORITHM_BY_METHOD_NAME = ImmutableMap.<String, String>builder()
          .put("getMd5Digest", MD5)
          .put("getShaDigest", SHA1)
          .put("getSha1Digest", SHA1)
          .put("md5", MD5)
          .put("md5Hex", MD5)
          .put("sha1", SHA1)
          .put("sha1Hex", SHA1)
          .put("sha", SHA1)
          .put("shaHex", SHA1)
          .build();

  @Override
  protected List<MethodMatcher> getMethodInvocationMatchers() {
    Builder<MethodMatcher> builder = ImmutableList.<MethodMatcher>builder()
            .add(MethodMatcher.create()
                    .typeDefinition("java.security.MessageDigest")
                    .name("getInstance")
                    .addParameter(JAVA_LANG_STRING))
            .add(MethodMatcher.create()
                    .typeDefinition("java.security.MessageDigest")
                    .name("getInstance")
                    .addParameter(JAVA_LANG_STRING)
                    .addParameter(TypeCriteria.anyType()))
            .add(MethodMatcher.create()
                    .typeDefinition("org.apache.commons.codec.digest.DigestUtils")
                    .name("getDigest")
                    .addParameter(JAVA_LANG_STRING));
    for (String methodName : ALGORITHM_BY_METHOD_NAME.keySet()) {
      builder.add(MethodMatcher.create()
              .typeDefinition("org.apache.commons.codec.digest.DigestUtils")
              .name(methodName)
              .withNoParameterConstraint());
    }
    for (String methodName : ImmutableList.of("md5", "sha1")) {
      builder.add(MethodMatcher.create()
              .typeDefinition("com.google.common.hash.Hashing")
              .name(methodName));
    }
    return builder.build();
  }

  @Override
  protected void onMethodInvocationFound(MethodInvocationTree mit) {
      System.out.println();
    String methodName = ((IdentifierTree) ((MemberSelectExpressionTree)mit.methodSelect()).expression()).symbolType().name();// MethodsHelper.methodName(mit).name();
    String algorithm = ALGORITHM_BY_METHOD_NAME.get(methodName);
    if (algorithm == null) {
      algorithm = algorithm(mit.arguments().get(0));
    }
    boolean isMd5 = MD5.equalsIgnoreCase(algorithm);
    boolean isSha1 = SHA1.equalsIgnoreCase(algorithm);
    if (isMd5 || isSha1) {
      String msgAlgo = isSha1 ? "SHA-1" : algorithm;
      addIssue(mit, "Use a stronger hashing algorithm than " + msgAlgo + ".");
    }
  }

  private static String algorithm(ExpressionTree invocationArgument) {
    ExpressionTree expectedAlgorithm = invocationArgument;
    ExpressionTree defaultPropertyValue = JavaPropertiesHelper.retrievedPropertyDefaultValue(invocationArgument);
    if (defaultPropertyValue != null) {
      expectedAlgorithm = defaultPropertyValue;
    }
    if (expectedAlgorithm.is(Tree.Kind.STRING_LITERAL)) {
      String algo = LiteralUtils.trimQuotes(((LiteralTree) expectedAlgorithm).value());
      return algo.replaceAll("-", "");
    }
    return null;
  }

}