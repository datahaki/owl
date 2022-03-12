// code by jph
package ch.alpine.sophus.demo.avg;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class GeodesicCenterSplitsDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GeodesicCenterSplitsDemo());
  }
}
