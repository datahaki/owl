// code by jph
package ch.alpine.sophus.demo.opt;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class SphereFitDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SphereFitDemo());
  }
}
