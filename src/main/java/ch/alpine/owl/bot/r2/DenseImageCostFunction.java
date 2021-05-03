// code by jph
package ch.alpine.owl.bot.r2;

import java.util.List;

import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** current implementation uses 2d image to store costs
 * a given trajectory is mapped to the pixels and costs are
 * weighted according to the traverse time */
public final class DenseImageCostFunction extends ImageCostFunction {
  public DenseImageCostFunction(Tensor image, Tensor range, Scalar outside) {
    super(image, range, outside);
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    Tensor dts = StateTimeTrajectories.deltaTimes(glcNode, trajectory);
    Tensor cost = Tensor.of(trajectory.stream() //
        .map(StateTime::state) //
        .map(flipYXTensorInterp::at));
    return (Scalar) cost.dot(dts);
  }
}
