// code by jph
package ch.alpine.owl.bot.r2;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import ch.alpine.owl.math.ImageGradient;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.itp.NearestInterpolation;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.N;

/** rotated gradient of potential function */
public class ImageGradientInterpolation implements Serializable {
  private static final Tensor ZEROS = Tensors.vectorDouble(0, 0).unmodifiable();

  /** @param image
   * @param range
   * @param amp
   * @return high quality continuous interpolation (slower than nearest) */
  public static ImageGradientInterpolation linear(Tensor image, Tensor range, Scalar amp) {
    return new ImageGradientInterpolation(image, range, amp, LinearInterpolation::of);
  }

  /** @param image
   * @param range
   * @param amp
   * @return fast discontinuous interpolation (fast) */
  public static ImageGradientInterpolation nearest(Tensor image, Tensor range, Scalar amp) {
    return new ImageGradientInterpolation(image, range, amp, NearestInterpolation::of);
  }

  // ---
  private final Tensor scale;
  private final Interpolation interpolation;
  private final Scalar maxNormGradient;

  /** @param render with rank 2. For instance, Dimensions.of(image) == [179, 128]
   * @param range with length() == 2
   * @param amp factor */
  private ImageGradientInterpolation(Tensor _image, Tensor range, Scalar amp, Function<Tensor, Interpolation> function) {
    Tensor image = _displayOrientation(_image);
    MatrixQ.require(image);
    List<Integer> dims = Dimensions.of(image);
    scale = Times.of(Tensors.vector(dims), range.map(Scalar::reciprocal));
    Tensor field = ImageGradient.rotated(image).multiply(N.DOUBLE.apply(amp));
    interpolation = function.apply(field);
    maxNormGradient = field.flatten(1).map(Vector2Norm::of).reduce(Max::of).orElseThrow();
  }

  /** @param vector of length 2
   * @return potentially unmodifiable */
  public Tensor get(Tensor vector) {
    Tensor index = Times.of(vector, scale);
    try {
      return interpolation.get(index);
    } catch (Exception exception) {
      // index is out of bounds
    }
    return ZEROS;
  }

  /** @return max(||gradient||) */
  public Scalar maxNormGradient() {
    return maxNormGradient;
  }

  // helper function
  private static Tensor _displayOrientation(Tensor tensor) {
    return Transpose.of(Reverse.of(tensor)); // flip y's, then swap x-y
  }
}
