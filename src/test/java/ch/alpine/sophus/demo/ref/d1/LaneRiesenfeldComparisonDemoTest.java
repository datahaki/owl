// code by jph
package ch.alpine.sophus.demo.ref.d1;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class LaneRiesenfeldComparisonDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new LaneRiesenfeldComparisonDemo());
  }
}
