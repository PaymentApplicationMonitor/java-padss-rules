package lk.ac.mrt.cse.padss.checks;

/**
 * Created by dewmal on 11/11/15.
 */
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
