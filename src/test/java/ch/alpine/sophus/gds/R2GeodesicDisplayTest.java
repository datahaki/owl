// code by jph
package ch.alpine.sophus.gds;

import ch.alpine.sophus.lie.rn.RnGeodesic;
import junit.framework.TestCase;

public class R2GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(R2Display.INSTANCE.geodesic(), RnGeodesic.INSTANCE);
  }
}
