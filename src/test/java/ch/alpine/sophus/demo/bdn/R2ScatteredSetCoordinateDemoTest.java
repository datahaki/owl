// code by jph
package ch.alpine.sophus.demo.bdn;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class R2ScatteredSetCoordinateDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R2ScatteredSetCoordinateDemo());
  }
}
