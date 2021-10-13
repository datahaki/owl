// code by jph
package ch.alpine.sophus.demo.bd1;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class S2AveragingDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new S2AveragingDemo());
  }
}
