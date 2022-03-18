// code by jph
package ch.alpine.sophus.ext.dis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.se2c.Se2CoveringGroup;

public class Se2CoveringDisplayTest {
  @Test
  public void testSimple() {
    assertEquals(Se2CoveringDisplay.INSTANCE.lieGroup(), Se2CoveringGroup.INSTANCE);
  }
}
