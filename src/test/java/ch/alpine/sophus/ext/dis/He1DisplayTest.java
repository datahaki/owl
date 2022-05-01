// code by jph
package ch.alpine.sophus.ext.dis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.he.HeGeodesic;

class He1DisplayTest {
  @Test
  public void testSimple() {
    assertEquals(He1Display.INSTANCE.geodesic(), HeGeodesic.INSTANCE);
  }
}
