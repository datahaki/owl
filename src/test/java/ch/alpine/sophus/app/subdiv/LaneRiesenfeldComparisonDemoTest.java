// code by jph
package ch.alpine.sophus.app.subdiv;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class LaneRiesenfeldComparisonDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new LaneRiesenfeldComparisonDemo());
  }
}
