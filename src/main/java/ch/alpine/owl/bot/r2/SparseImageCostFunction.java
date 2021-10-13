// code by ynager
package ch.alpine.owl.bot.r2;

import java.util.List;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Lists;

/** slightly different from {@link ImageCostFunction}
 * because evaluation only happens at last state of trajectory */
public final class SparseImageCostFunction extends ImageCostFunction {
  public SparseImageCostFunction(Tensor image, Tensor range, Scalar outside) {
    super(image, range, outside);
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return flipYXTensorInterp.at(Lists.last(trajectory).state());
  }
}
