// code by jph
package ch.alpine.sophus.app.bdn;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class R2BarycentricCoordinateDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R2BarycentricCoordinateDemo());
  }
}
