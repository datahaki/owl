// code by jph
package ch.ethz.idsc.sophus.app.jph;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class R1BarycentricDegreeDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R1BarycentricDegreeDemo());
  }
}
