// code by jph
package ch.alpine.sophus.app.hermite;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class SeriesHermiteSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SeriesHermiteSubdivisionDemo());
  }
}
