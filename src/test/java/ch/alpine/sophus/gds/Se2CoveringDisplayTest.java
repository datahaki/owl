// code by jph
package ch.alpine.sophus.gds;

import ch.alpine.sophus.lie.se2c.Se2CoveringGroup;
import junit.framework.TestCase;

public class Se2CoveringDisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(Se2CoveringDisplay.INSTANCE.lieGroup(), Se2CoveringGroup.INSTANCE);
  }
}
