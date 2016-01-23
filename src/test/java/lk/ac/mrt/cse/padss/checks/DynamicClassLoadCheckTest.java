
package lk.ac.mrt.cse.padss.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class DynamicClassLoadCheckTest {

  @Test
  public void test() {
    JavaCheckVerifier.verify("src/test/files/checks/DynamicClassLoadCheck.java", new DynamicClassLoadCheck());
  }

}
