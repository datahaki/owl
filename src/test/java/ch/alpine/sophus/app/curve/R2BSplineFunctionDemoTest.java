// code by jph
package ch.alpine.sophus.app.curve;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class R2BSplineFunctionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R2BSplineFunctionDemo());
  }
}
