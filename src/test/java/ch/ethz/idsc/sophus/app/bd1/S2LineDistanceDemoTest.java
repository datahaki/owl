// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class S2LineDistanceDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new S2LineDistanceDemo());
  }
}
