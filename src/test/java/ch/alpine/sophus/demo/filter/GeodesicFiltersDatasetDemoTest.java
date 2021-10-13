// code by jph
package ch.alpine.sophus.demo.filter;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class GeodesicFiltersDatasetDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GeodesicFiltersDatasetDemo());
  }
}
