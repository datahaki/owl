// code by jph
package ch.alpine.sophus.app.dubins;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class DubinsPathDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new DubinsPathDemo());
  }
}
