// code by jph
package ch.alpine.owl.math.region;

import java.io.IOException;

import ch.alpine.owl.bot.r2.R2ExamplePolygons;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class PolygonRegionTest extends TestCase {
  public void testPolygon() throws ClassNotFoundException, IOException {
    PolygonRegion polygonRegion = (PolygonRegion) Serialization.copy(PolygonRegion.numeric(R2ExamplePolygons.BULKY_TOP_LEFT));
    assertEquals(polygonRegion.polygon(), R2ExamplePolygons.BULKY_TOP_LEFT);
  }

  public void testInside() {
    PolygonRegion polygonRegion = new PolygonRegion(R2ExamplePolygons.BULKY_TOP_LEFT);
    assertFalse(polygonRegion.isMember(Tensors.vector(-1, 1)));
    assertFalse(polygonRegion.isMember(Tensors.vector(1, 1)));
    assertTrue(polygonRegion.isMember(Tensors.vector(3, 5)));
  }
}
