package lk.ac.mrt.cse.padss.checks;


import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SQLMethodsInSecureObjectCheckTest {
    @Test
    public void detected() {

        SQLMethodsInSecureObjectCheck check = new SQLMethodsInSecureObjectCheck();
        check.className="padssDataObject";

        JavaCheckVerifier.verify("src/test/files/padssSecureObjectWithSQL.java", check);
    }
}
