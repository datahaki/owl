// code by jph
package ch.alpine.sophus.demo.curve;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class BarycentricRationalInterpolationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BarycentricRationalInterpolationDemo());
  }
}
