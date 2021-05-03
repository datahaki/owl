// code by jl
package ch.alpine.owl.bot.se2;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

/** cost function that penalizes the switching between forwards and backwards driving */
// DO NOT MODIFY THIS CLASS SINCE THE FUNCTIONALITY IS USED IN MANY DEMOS
public final class Se2ShiftCostFunction implements CostFunction, Serializable {
  private final Scalar shiftPenalty;

  public Se2ShiftCostFunction(Scalar shiftPenalty) {
    this.shiftPenalty = shiftPenalty;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    Tensor ante = glcNode.flow(); // == null if glcNode is root
    return Objects.nonNull(ante) && Sign.isNegative(ante.Get(0).multiply(flow.Get(0))) //
        ? shiftPenalty
        : shiftPenalty.zero();
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    return RealScalar.ZERO;
  }
}
