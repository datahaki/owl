// code by jph
package ch.alpine.sophus.demo.bdn;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class Se2DeformationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Se2DeformationDemo());
  }
}
