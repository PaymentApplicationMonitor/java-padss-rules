package lk.ac.mrt.cse.padss;

import java.util.Arrays;

import lk.ac.mrt.cse.padss.checks.*;
import org.sonar.plugins.java.api.CheckRegistrar;
import org.sonar.plugins.java.api.JavaCheck;

/**
 * Provide the "checks" (implementations of rules) classes that are gonna be executed during
 * source code analysis.
 *
 * This class is a batch extension by implementing the {@link org.sonar.plugins.java.api.CheckRegistrar} interface.
 */
public class MyJavaFileCheckRegistrar implements CheckRegistrar {

  /**
   * Register the classes that will be used to instantiate checks during analysis.
   */
  @Override
  public void register(RegistrarContext registrarContext) {
    // Call to registerClassesForRepository to associate the classes with the correct repository key
    registrarContext.registerClassesForRepository(MyJavaRulesDefinition.REPOSITORY_KEY, Arrays.asList(checkClasses()), Arrays.asList(testCheckClasses()));
  }

  /**
   * Lists all the checks provided by the plugin
   */
  public static Class<? extends JavaCheck>[] checkClasses() {
    return new Class[] {
      SecurityAnnotationMandatoryCheck.class,
      MyCustomSubscriptionRule.class,
      AvoidAnnotationCheck.class,
      AvoidMethodDeclarationCheck.class,
      AvoidBrandInMethodNamesCheck.class,
            HTTPCheck.class
      };
  }

  /**
   * Lists all the test checks provided by the plugin
   */
  public static Class<? extends JavaCheck>[] testCheckClasses() {
    return new Class[] {};
  }
}
