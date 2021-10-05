// code by jph
package ch.alpine.sophus.app.curve;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class HeadTailGeodesicDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new HeadTailGeodesicDemo());
  }
}
