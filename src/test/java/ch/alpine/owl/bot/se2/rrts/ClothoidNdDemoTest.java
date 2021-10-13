// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class ClothoidNdDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClothoidNdDemo());
  }
}
