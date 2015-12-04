package lk.ac.mrt.cse.padss.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SQLInjectionCheckTest {

    @Test
    public void test() {
        JavaCheckVerifier.verify("src/test/files/SQLInjection.java", new SQLInjectionCheck());
    }

}
