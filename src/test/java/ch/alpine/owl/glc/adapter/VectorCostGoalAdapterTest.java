// code by jph
package ch.alpine.owl.glc.adapter;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.bot.se2.Se2MinTimeGoalManager;
import ch.alpine.owl.bot.se2.glc.Se2CarFlows;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.VectorScalar;
import ch.alpine.owl.math.region.Regions;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.sca.Chop;

class VectorCostGoalAdapterTest {
  @Test
  public void testSimple() {
    List<CostFunction> costs = new ArrayList<>();
    costs.add(new Se2MinTimeGoalManager( //
        Se2ComboRegion.ball(Tensors.vector(2, 1, Math.PI * -1), Tensors.vector(0.1, 0.1, 10 / 180 * Math.PI)), //
        Se2CarFlows.standard(RealScalar.of(1), Degree.of(35)).getFlows(10)));
    costs.add(new Se2MinTimeGoalManager( //
        Se2ComboRegion.ball(Tensors.vector(2, 1, Math.PI * -1), Tensors.vector(0.1, 0.1, 10 / 180 * Math.PI)), //
        Se2CarFlows.standard(RealScalar.of(2), Degree.of(35)).getFlows(10)));
    GoalInterface goalInterface = new VectorCostGoalAdapter(costs, Regions.emptyRegion());
    Scalar minCostToGoal = goalInterface.minCostToGoal(Tensors.vector(0, 0, 0));
    VectorScalar vs = (VectorScalar) minCostToGoal;
    Tensor vector = vs.vector();
    Chop._13.requireClose(vector.Get(0).subtract(vector.Get(1)), vector.Get(1));
  }
}
