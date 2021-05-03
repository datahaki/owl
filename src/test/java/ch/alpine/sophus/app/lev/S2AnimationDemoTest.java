// code by jph
package ch.alpine.sophus.app.lev;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class S2AnimationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new S2AnimationDemo());
  }
}
