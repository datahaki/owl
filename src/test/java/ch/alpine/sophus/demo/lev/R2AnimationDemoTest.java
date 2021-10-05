// code by jph
package ch.alpine.sophus.demo.lev;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class R2AnimationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R2AnimationDemo());
  }
}
