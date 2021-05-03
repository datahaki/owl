// code by jph
package ch.alpine.owl.bot.rn;

import java.util.Collection;

import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class RnMinTimeGoalManagerTest extends TestCase {
  public void testSimple() {
    R2Flows r2Flows = new R2Flows(Quantity.of(2, "m*s^-1"));
    Collection<Tensor> controls = r2Flows.getFlows(10);
    Tensor center = Tensors.fromString("{3[m], 6[m]}");
    Scalar radius = Quantity.of(1, "m");
    RegionWithDistance<Tensor> regionWithDistance = new BallRegion(center, radius);
    GoalInterface goalInterface = RnMinTimeGoalManager.create(regionWithDistance, controls);
    // Scalar cost = ;
    assertEquals(goalInterface.minCostToGoal(Tensors.fromString("{3[m], 6[m]}")), Quantity.of(0, "s"));
    assertEquals(goalInterface.minCostToGoal(Tensors.fromString("{2[m], 6[m]}")), Quantity.of(0, "s"));
    Chop._14.requireClose(goalInterface.minCostToGoal( //
        Tensors.fromString("{0[m], 6[m]}")), Quantity.of(1, "s"));
  }
}
