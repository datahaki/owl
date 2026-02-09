// code by jph
package ch.alpine.owl.bot.r2;

import java.io.Serializable;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.region.FlipYXTensorInterp;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

public abstract class ImageCostFunction implements CostFunction, Serializable {
  private final Tensor image;
  private final Tensor range;
  protected final FlipYXTensorInterp<Scalar> flipYXTensorInterp;

  /** @param image as a matrix
   * @param range effective size of image in coordinate space
   * @param outside point member status */
  protected ImageCostFunction(Tensor image, Tensor range, Scalar outside) {
    this.image = MatrixQ.require(image);
    this.range = VectorQ.requireLength(range, 2);
    flipYXTensorInterp = new FlipYXTensorInterp<>(image, range, value -> value, outside);
  }

  @Override // from HeuristicFunction
  public final Scalar minCostToGoal(Tensor tensor) {
    return RealScalar.ZERO;
  }

  public final Tensor image() {
    return image.unmodifiable();
  }

  public final CoordinateBoundingBox range() {
    return CoordinateBounds.of(range.maps(Scalar::zero), range);
  }

  public final Tensor scale() {
    return flipYXTensorInterp.scale();
  }
}
