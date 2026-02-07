// code by jph
package ch.alpine.owl.glc.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophis.crv.d2.alg.PolygonRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

class ConstraintViolationCostTest {
  @Test
  void testSimple() {
    Tensor polygon = Tensors.matrixInt(new int[][] { { 1, 0 }, { 4, 0 }, { 4, 3 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction costFunction = new ConstraintViolationCost(plannerConstraint, Quantity.of(10, "CHF"));
    assertTrue(polygonRegion.test(Tensors.vector(3, 1)));
    boolean isSatisfied = //
        plannerConstraint.isSatisfied(null, Arrays.asList(new StateTime(Tensors.vector(3, 1), RealScalar.ZERO)), null);
    assertFalse(isSatisfied);
    assertEquals( //
        costFunction.costIncrement(null, Arrays.asList(new StateTime(Tensors.vector(3, 1), RealScalar.ZERO)), null), //
        Quantity.of(10, "CHF"));
    assertEquals(costFunction.costIncrement( //
        null, Arrays.asList(new StateTime(Tensors.vector(1, 1), RealScalar.ZERO)), null), //
        Quantity.of(0, "CHF"));
    assertEquals(costFunction.minCostToGoal(null), Quantity.of(0.0, "CHF"));
  }
}
