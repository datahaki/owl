// code by jph
package ch.alpine.sophus.demo.opt;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class RigidMotionFitDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new RigidMotionFitDemo());
  }
}
