// code by jph
package ch.alpine.sophus.demo.avg;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class BezierFunctionSplitsDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BezierFunctionSplitsDemo());
  }
}
