// code by jph
package ch.alpine.owl.bot.r2;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.noise.SimplexContinuousNoise;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Ramp;

/** cost function with unit time
 * 
 * the cost increment may be zero therefore, min cost to goal also is zero */
public class R2NoiseCostFunction implements CostFunction, Serializable {
  // ---
  private final Scalar treshold;

  public R2NoiseCostFunction(Scalar treshold) {
    this.treshold = treshold;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    Tensor dts = StateTimeTrajectories.deltaTimes(glcNode, trajectory);
    Tensor cost = Tensor.of(trajectory.stream() //
        .map(StateTime::state) //
        .map(SimplexContinuousNoise.FUNCTION) // used to be 2D
        .map(scalar -> scalar.subtract(treshold)) //
        .map(Ramp.FUNCTION));
    return (Scalar) cost.dot(dts);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    return RealScalar.ZERO;
  }
}
