// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class BSplineBasisDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BSplineBasisDemo());
  }
}
