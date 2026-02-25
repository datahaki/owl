// code by ynager
package ch.alpine.owl.bot.r2;

import java.util.List;

import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** slightly different from {@link ImageCostFunction}
 * because evaluation only happens at last state of trajectory */
public final class SparseImageCostFunction extends ImageCostFunction {
  public SparseImageCostFunction(Tensor image, Tensor range, Scalar outside) {
    super(image, range, outside);
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return flipYXTensorInterp.at(trajectory.getLast().state());
  }
}
