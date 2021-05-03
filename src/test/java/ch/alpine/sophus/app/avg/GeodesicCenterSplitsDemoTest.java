// code by jph
package ch.alpine.sophus.app.avg;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class GeodesicCenterSplitsDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GeodesicCenterSplitsDemo());
  }
}
