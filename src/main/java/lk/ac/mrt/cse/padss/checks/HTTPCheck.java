package lk.ac.mrt.cse.padss.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.squidbridge.annotations.ActivatedByDefault;

import java.util.regex.Pattern;

@Rule(
        key = "AvoidNon-SecureURLs",
        name = "Avoid usage of non-secure URLs",
        description = "This rule detects usage of non-secure URLs",
        tags = {"pa-dss"})
@ActivatedByDefault
public class HTTPCheck extends BaseTreeVisitor implements JavaFileScanner {

    private static final String DEFAULT_FORMAT = "^(http?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    public String format = DEFAULT_FORMAT;
    private JavaFileScannerContext context;
    private Pattern pattern = null;

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        if (pattern == null) {
            pattern = Pattern.compile(format, Pattern.DOTALL);
        }
        this.context = context;
        scan(context.getTree());
    }

    @Override
    public void visitLiteral(LiteralTree tree) {
        if (tree.value().length() > 3) {
            String temp = tree.value().substring(1, tree.value().length() - 1);
            if (pattern.matcher(temp).matches()) {
                context.addIssue(tree, this, String.format("Avoid using non-secure urls %s", temp));
            }
        }
        super.visitLiteral(tree);
    }
}

