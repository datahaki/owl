// code by jph
package ch.alpine.sophus.demo.bd1;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class R1KrigingDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R1KrigingDemo());
  }
}
