// code by jph
package ch.alpine.owl.region;

import java.io.Serializable;
import java.util.List;

import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.red.Times;

/** only the first two coordinates are tested for membership
 * a location is available if the grayscale value of the pixel equals 0
 * 
 * Hint: the use of {@link BufferedImageRegion} is preferred. */
public class ImageRegion implements Region<Tensor>, RegionBounds, Serializable {
  private final Tensor image;
  private final Tensor range;
  private final Tensor scale;
  private final FlipYXTensorInterp<Boolean> flipYXTensorInterp;

  /** @param image has to be a matrix
   * @param range effective size of image in coordinate space, vector of length 2
   * @param outside point member status */
  // TODO OWL API still needed next to BufferedImageRegion
  public ImageRegion(Tensor image, Tensor range, boolean outside) {
    this.image = MatrixQ.require(image);
    List<Integer> dimensions = Dimensions.of(image);
    int dim0 = dimensions.get(0);
    int dim1 = dimensions.get(1);
    this.range = range;
    scale = Times.of(Tensors.vector(dim1, dim0), range.map(Scalar::reciprocal));
    flipYXTensorInterp = new FlipYXTensorInterp<>(image, range, Scalars::nonZero, outside);
  }

  @Override // from Region
  public boolean test(Tensor tensor) {
    return flipYXTensorInterp.at(tensor);
  }

  public Tensor image() {
    return image.unmodifiable();
  }

  public Tensor range() {
    return range.unmodifiable();
  }

  public Tensor scale() {
    return scale.unmodifiable();
  }

  public Tensor origin() {
    return range.map(Scalar::zero);
  }

  @Override
  public CoordinateBoundingBox coordinateBounds() {
    return CoordinateBounds.of(origin(), range());
  }
}
