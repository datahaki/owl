// code by jph
package ch.alpine.sophus.demo.lev;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class WeightsDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new WeightsDemo());
  }
}
