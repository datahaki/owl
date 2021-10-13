// code by jph
package ch.alpine.sophus.demo.clt;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class ClothoidComparisonDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClothoidComparisonDemo());
  }
}
