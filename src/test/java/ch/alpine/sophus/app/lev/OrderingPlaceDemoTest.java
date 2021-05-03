// code by jph
package ch.alpine.sophus.app.lev;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class OrderingPlaceDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new OrderingPlaceDemo());
  }
}
