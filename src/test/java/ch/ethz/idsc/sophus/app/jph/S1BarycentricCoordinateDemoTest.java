// code by jph
package ch.ethz.idsc.sophus.app.jph;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class S1BarycentricCoordinateDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new S1BarycentricCoordinateDemo());
  }
}
