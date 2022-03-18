// code by jph
package ch.alpine.sophus.ext.dis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.rn.RnGeodesic;

public class R2DisplayTest {
  @Test
  public void testSimple() {
    assertEquals(R2Display.INSTANCE.geodesic(), RnGeodesic.INSTANCE);
  }
}
