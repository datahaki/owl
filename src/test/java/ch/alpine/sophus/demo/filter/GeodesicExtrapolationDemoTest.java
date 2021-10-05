// code by jph
package ch.alpine.sophus.demo.filter;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class GeodesicExtrapolationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GeodesicExtrapolationDemo());
  }
}
