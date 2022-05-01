// code by jph
package ch.alpine.owl.math.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;

class LinearRegionTest {
  @Test
  public void testSimple() {
    LinearRegion linearRegion = new LinearRegion(RealScalar.of(10), RealScalar.of(2));
    assertEquals(linearRegion.center(), RealScalar.of(10));
    assertEquals(linearRegion.radius(), RealScalar.of(2));
    assertEquals(linearRegion.signedDistance(RealScalar.of(7)), RealScalar.of(1));
    assertEquals(linearRegion.signedDistance(RealScalar.of(9)), RealScalar.of(-1));
    assertEquals(linearRegion.signedDistance(RealScalar.of(10)), RealScalar.of(-2));
    assertEquals(linearRegion.signedDistance(RealScalar.of(11)), RealScalar.of(-1));
    assertTrue(ExactScalarQ.of(linearRegion.signedDistance(RealScalar.of(11))));
    assertEquals(linearRegion.distance(RealScalar.of(7)), RealScalar.of(1));
    assertEquals(linearRegion.distance(RealScalar.of(9)), RealScalar.of(0));
    assertEquals(linearRegion.distance(RealScalar.of(10)), RealScalar.of(0));
    assertEquals(linearRegion.distance(RealScalar.of(11)), RealScalar.of(0));
    assertTrue(ExactScalarQ.of(linearRegion.distance(RealScalar.of(11))));
  }

  @Test
  public void testQuantity() {
    LinearRegion linearRegion = new LinearRegion(Quantity.of(10, "m"), Quantity.of(2, "m"));
    assertEquals(linearRegion.signedDistance(Quantity.of(7, "m")), Quantity.of(1, "m"));
    assertEquals(linearRegion.signedDistance(Quantity.of(9, "m")), Quantity.of(-1, "m"));
    assertTrue(ExactScalarQ.of(linearRegion.distance(Quantity.of(11, "m"))));
  }

  @Test
  public void testClip() {
    LinearRegion linearRegion = new LinearRegion(Quantity.of(10, "m"), Quantity.of(2, "m"));
    Clip clip = linearRegion.clip();
    assertEquals(clip.min(), Quantity.of(8, "m"));
    assertEquals(clip.max(), Quantity.of(12, "m"));
  }

  @Test
  public void testFail() {
    assertThrows(Exception.class, () -> new LinearRegion(null, RealScalar.of(2)));
    assertThrows(Exception.class, () -> new LinearRegion(RealScalar.of(2), RealScalar.of(-3)));
  }
}
