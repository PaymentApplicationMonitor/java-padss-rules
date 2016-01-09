
package lk.ac.mrt.cse.padss;

import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * Entry point of plugin
 */
public class PADSSRulesPlugin extends SonarPlugin {

  @Override
  public List getExtensions() {
    return Arrays.asList(
      // server extensions -> objects are instantiated during server startup
      PADSSRulesDefinition.class,

      // batch extensions -> objects are instantiated during code analysis
      PADSSFileCheckRegistrar.class);
  }

}
