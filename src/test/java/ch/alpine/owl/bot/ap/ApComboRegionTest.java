// code by astoll
package ch.alpine.owl.bot.ap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.region.LinearRegion;
import ch.alpine.owl.math.region.So2Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

public class ApComboRegionTest {
  @Test
  public void testSimple() {
    ApComboRegion apComboRegion = new ApComboRegion( //
        new LinearRegion(Quantity.of(5, "m"), Quantity.of(1, "m")), //
        new LinearRegion(Quantity.of(50, "m*s^-1"), Quantity.of(10, "m*s^-1")), //
        So2Region.periodic(RealScalar.of(0.1), RealScalar.of(0.05)));
    assertTrue(apComboRegion.test(Tensors.fromString("{1000[m], 5[m], 45[m*s^-1], 0.05}")));
  }

  @Test
  public void testD_z() {
    Tensor goalRegionTest = Tensors.of(Quantity.of(5, "m"), Quantity.of(50, "m*s^-1"), RealScalar.of(0.1));
    Tensor radiusVectorTest = Tensors.of(Quantity.of(1, "m"), Quantity.of(10, "m*s^-1"), RealScalar.of(0.05));
    ApComboRegion apComboRegionConstructed = ApComboRegion.createApRegion(goalRegionTest, radiusVectorTest);
    Scalar distanceExpected = Quantity.of(4, "m");
    assertEquals(distanceExpected, apComboRegionConstructed.d_z(Tensors.fromString("{1000[m], 10[m], 45[m*s^-1], 0.05}")));
  }

  @Test
  public void testRequireNonNull() {
    AssertFail.of(() -> ApComboRegion.createApRegion(null, null));
  }
}
