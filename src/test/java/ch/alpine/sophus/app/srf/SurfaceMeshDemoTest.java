// code by jph
package ch.alpine.sophus.app.srf;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class SurfaceMeshDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SurfaceMeshDemo());
  }
}
