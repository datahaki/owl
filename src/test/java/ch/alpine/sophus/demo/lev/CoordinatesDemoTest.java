// code by jph
package ch.alpine.sophus.demo.lev;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class CoordinatesDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new CoordinatesDemo());
  }
}
