// code by jph
package ch.alpine.sophus.demo.curve;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class BSplineBasisDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BSplineBasisDemo());
  }
}
