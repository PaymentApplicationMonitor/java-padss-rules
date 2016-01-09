package lk.ac.mrt.cse.padss.checks;


import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SecureObjectToStringCheckTest {
    @Test
    public void detected() {

        SecureObjectToStringCheck check = new SecureObjectToStringCheck();
        check.className="padssDataObject";
        check.disallowedMethodName ="toString";

        JavaCheckVerifier.verify("src/test/files/SecureObjectToString.java", check);
    }
}
