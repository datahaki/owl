// code by jph
package ch.alpine.sophus.app.clt;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class SpearheadDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SpearheadDemo());
  }
}
