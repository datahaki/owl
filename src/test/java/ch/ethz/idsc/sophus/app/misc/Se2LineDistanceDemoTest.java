// code by jph
package ch.ethz.idsc.sophus.app.misc;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class Se2LineDistanceDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Se2LineDistanceDemo());
  }
}
