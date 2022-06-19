// code by astoll
package ch.alpine.owl.bot.ap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.region.LinearRegion;
import ch.alpine.owl.math.region.So2Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

class ApMinTimeGoalManagerTest {
  @Test
  void testSimple() {
    Scalar maxSpeed = Quantity.of(83, "m*s^-1");
    ApComboRegion apComboRegion = new ApComboRegion( //
        new LinearRegion(Quantity.of(5, "m"), Quantity.of(1, "m")), //
        new LinearRegion(Quantity.of(50, "m*s^-1"), Quantity.of(10, "m*s^-1")), //
        So2Region.periodic(RealScalar.of(0.1), RealScalar.of(0.05)));
    ApMinTimeGoalManager apMinTimeGoalManager = new ApMinTimeGoalManager(apComboRegion, maxSpeed);
    Scalar expected = Quantity.of(4, "m").divide(maxSpeed);
    assertEquals(expected, apMinTimeGoalManager.minCostToGoal(Tensors.fromString("{1000[m], 10[m], 45[m*s^-1], 0.05}")));
  }
}
