package lk.ac.mrt.cse.padss.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class VariableEndCheckTest {
    @Test
    public void detected() {

        VariableEndCheck check = new VariableEndCheck();
        check.className="padssDataObject";
        check.discardMethodName="discard";

        JavaCheckVerifier.verify("src/test/files/padssDataObject.java", check);
    }
}
