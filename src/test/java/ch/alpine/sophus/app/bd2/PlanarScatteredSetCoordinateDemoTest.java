// code by jph
package ch.alpine.sophus.app.bd2;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class PlanarScatteredSetCoordinateDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new PlanarScatteredSetCoordinateDemo());
  }
}
