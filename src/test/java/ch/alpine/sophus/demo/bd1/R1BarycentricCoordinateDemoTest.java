// code by jph
package ch.alpine.sophus.demo.bd1;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class R1BarycentricCoordinateDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R1BarycentricCoordinateDemo());
  }
}
