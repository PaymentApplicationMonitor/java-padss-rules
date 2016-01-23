
package lk.ac.mrt.cse.padss.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SecureCookieCheckTest {

  @Test
  public void detected() {
    JavaCheckVerifier.verify("src/test/files/checks/SecureCookieCheck.java", new SecureCookieCheck());
  }
}
