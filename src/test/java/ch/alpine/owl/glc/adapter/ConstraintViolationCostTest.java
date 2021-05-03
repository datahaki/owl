// code by jph
package ch.alpine.owl.glc.adapter;

import java.util.Arrays;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.region.PolygonRegion;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ConstraintViolationCostTest extends TestCase {
  public void testSimple() {
    Tensor polygon = Tensors.matrixInt(new int[][] { { 1, 0 }, { 4, 0 }, { 4, 3 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction costFunction = ConstraintViolationCost.of(plannerConstraint, Quantity.of(10, "CHF"));
    assertTrue(polygonRegion.isMember(Tensors.vector(3, 1)));
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
