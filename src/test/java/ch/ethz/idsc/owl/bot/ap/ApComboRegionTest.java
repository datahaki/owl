// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.owl.math.region.LinearRegion;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ApComboRegionTest extends TestCase {
  public void testSimple() {
    ApComboRegion apComboRegion = new ApComboRegion( //
        new LinearRegion(Quantity.of(5, "m"), Quantity.of(1, "m")), //
        new LinearRegion(Quantity.of(50, "m*s^-1"), Quantity.of(10, "m*s^-1")), //
        So2Region.periodic(RealScalar.of(0.1), RealScalar.of(0.05)));
    assertTrue(apComboRegion.isMember(Tensors.fromString("{1000[m], 5[m], 45[m*s^-1], 0.05}")));
  }

  public void testD_z() {
    Tensor goalRegionTest = Tensors.of(Quantity.of(5, "m"), Quantity.of(50, "m*s^-1"), RealScalar.of(0.1));
    Tensor radiusVectorTest = Tensors.of(Quantity.of(1, "m"), Quantity.of(10, "m*s^-1"), RealScalar.of(0.05));
    ApComboRegion apComboRegionConstructed = ApComboRegion.createApRegion(goalRegionTest, radiusVectorTest);
    Scalar distanceExpected = Quantity.of(4, "m");
    assertEquals(distanceExpected, apComboRegionConstructed.d_z(Tensors.fromString("{1000[m], 10[m], 45[m*s^-1], 0.05}")));
  }

  public void testRequireNonNull() {
    AssertFail.of(() -> ApComboRegion.createApRegion(null, null));
  }
}
