// code by jph
package ch.alpine.sophus.ext.dis;

import ch.alpine.sophus.ext.dis.He1Display;
import ch.alpine.sophus.lie.he.HeGeodesic;
import junit.framework.TestCase;

public class He1DisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(He1Display.INSTANCE.geodesic(), HeGeodesic.INSTANCE);
  }
}
