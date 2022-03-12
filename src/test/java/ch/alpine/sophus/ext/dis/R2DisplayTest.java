// code by jph
package ch.alpine.sophus.ext.dis;

import ch.alpine.sophus.ext.dis.R2Display;
import ch.alpine.sophus.lie.rn.RnGeodesic;
import junit.framework.TestCase;

public class R2DisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(R2Display.INSTANCE.geodesic(), RnGeodesic.INSTANCE);
  }
}
