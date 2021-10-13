// code by jph
package ch.alpine.sophus.demo.avg;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class ExtrapolationSplitsDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ExtrapolationSplitsDemo());
  }
}
