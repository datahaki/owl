// code by jph
package ch.alpine.sophus.gds;

import ch.alpine.sophus.lie.he.HeGeodesic;
import junit.framework.TestCase;

public class He1GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(He1Display.INSTANCE.geodesic(), HeGeodesic.INSTANCE);
  }
}
