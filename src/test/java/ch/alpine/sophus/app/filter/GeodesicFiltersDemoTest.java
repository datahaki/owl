// code by jph
package ch.alpine.sophus.app.filter;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class GeodesicFiltersDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GeodesicFiltersDemo());
  }
}
