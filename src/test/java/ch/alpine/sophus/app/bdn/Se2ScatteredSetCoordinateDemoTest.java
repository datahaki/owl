// code by jph
package ch.alpine.sophus.app.bdn;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class Se2ScatteredSetCoordinateDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Se2ScatteredSetCoordinateDemo());
  }
}
