// code by jph
package ch.alpine.sophus.app.misc;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class HilbertCurveDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new HilbertCurveDemo());
  }
}
