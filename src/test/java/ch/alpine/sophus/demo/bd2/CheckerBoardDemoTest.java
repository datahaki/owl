// code by jph
package ch.alpine.sophus.demo.bd2;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class CheckerBoardDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new CheckerBoardDemo());
  }
}
