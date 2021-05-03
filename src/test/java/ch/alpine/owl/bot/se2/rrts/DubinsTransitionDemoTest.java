// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class DubinsTransitionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new DubinsTransitionDemo());
  }
}
