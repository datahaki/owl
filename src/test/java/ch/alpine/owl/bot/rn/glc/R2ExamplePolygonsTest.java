// code by jph
package ch.alpine.owl.bot.rn.glc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.d2.alg.PolygonRegion;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class R2ExamplePolygonsTest {
  @Test
  void testPolygon() throws ClassNotFoundException, IOException {
    PolygonRegion polygonRegion = Serialization.copy(new PolygonRegion(R2ExamplePolygons.BULKY_TOP_LEFT));
    assertEquals(polygonRegion.polygon(), R2ExamplePolygons.BULKY_TOP_LEFT);
  }

  @Test
  void testInside() {
    PolygonRegion polygonRegion = new PolygonRegion(R2ExamplePolygons.BULKY_TOP_LEFT);
    assertFalse(polygonRegion.test(Tensors.vector(-1, 1)));
    assertFalse(polygonRegion.test(Tensors.vector(1, 1)));
    assertTrue(polygonRegion.test(Tensors.vector(3, 5)));
  }
}
