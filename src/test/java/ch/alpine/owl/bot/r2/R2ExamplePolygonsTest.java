// code by jph
package ch.alpine.owl.bot.r2;

import java.io.IOException;

import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class R2ExamplePolygonsTest extends TestCase {
  public void testPolygon() throws ClassNotFoundException, IOException {
    PolygonRegion polygonRegion = Serialization.copy(new PolygonRegion(R2ExamplePolygons.BULKY_TOP_LEFT));
    assertEquals(polygonRegion.polygon(), R2ExamplePolygons.BULKY_TOP_LEFT);
  }

  public void testInside() {
    PolygonRegion polygonRegion = new PolygonRegion(R2ExamplePolygons.BULKY_TOP_LEFT);
    assertFalse(polygonRegion.test(Tensors.vector(-1, 1)));
    assertFalse(polygonRegion.test(Tensors.vector(1, 1)));
    assertTrue(polygonRegion.test(Tensors.vector(3, 5)));
  }
}
