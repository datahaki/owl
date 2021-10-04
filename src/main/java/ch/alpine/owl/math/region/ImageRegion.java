// code by jph
package ch.alpine.owl.math.region;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.List;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.opt.nd.Box;

/** only the first two coordinates are tested for membership
 * a location is available if the grayscale value of the pixel equals 0
 * 
 * Hint: the use of {@link BufferedImageRegion} is preferred. */
public class ImageRegion implements Region<Tensor>, Serializable {
  /** @param bufferedImage
   * @param range
   * @param outside
   * @return */
  public static Region<Tensor> of(BufferedImage bufferedImage, Tensor range, boolean outside) {
    return new BufferedImageRegion(bufferedImage, //
        Tensors.vector( //
            range.Get(0).number().doubleValue() / bufferedImage.getWidth(), //
            range.Get(1).number().doubleValue() / bufferedImage.getHeight(), 1) //
            .pmul(GfxMatrix.flipY(bufferedImage.getHeight())),
        outside);
  }

  // ---
  private final Tensor image;
  private final Tensor range;
  private final Tensor scale;
  private final FlipYXTensorInterp<Boolean> flipYXTensorInterp;

  /** @param image has to be a matrix
   * @param range effective size of image in coordinate space, vector of length 2
   * @param outside point member status */
  public ImageRegion(Tensor image, Tensor range, boolean outside) {
    this.image = MatrixQ.require(image);
    List<Integer> dimensions = Dimensions.of(image);
    int dim0 = dimensions.get(0);
    int dim1 = dimensions.get(1);
    this.range = range;
    scale = Tensors.vector(dim1, dim0).pmul(range.map(Scalar::reciprocal));
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

  public Box box() {
    return Box.of(origin(), range());
  }
}
