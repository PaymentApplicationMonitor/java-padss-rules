package lk.ac.mrt.cse.padss.checks;

/**
 * Created by dewmal on 11/11/15.
 */
import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SecureObjectMethodsCheckTest {
    @Test
    public void detected() {

        SecureObjectMethodsCheck check = new SecureObjectMethodsCheck();
        check.className="padssDataObject";

        JavaCheckVerifier.verify("src/test/files/padssSecureObject.java", check);
    }
}