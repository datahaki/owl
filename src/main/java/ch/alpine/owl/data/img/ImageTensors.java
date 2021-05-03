// code by ynager
package ch.alpine.owl.data.img;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.TensorMap;

public class ImageTensors {
  private static final Scalar xFF = RealScalar.of(255);

  /** @param image
   * @param rgba
   * @return b/w image */
  public static Tensor reduce(Tensor image, Tensor rgba) {
    return TensorMap.of(color -> color.equals(rgba) ? xFF : RealScalar.ZERO, image, 2);
  }

  /** @param image
   * @param channel
   * @return b/w image */
  public static Tensor reduce(Tensor image, int channel) {
    return TensorMap.of(color -> Scalars.isZero(color.Get(channel)) ? RealScalar.ZERO : xFF, image, 2);
  }

  public static Tensor reduceInverted(Tensor image, Tensor rgba) {
    return TensorMap.of(color -> color.equals(rgba) ? RealScalar.ZERO : xFF, image, 2);
  }

  public static Tensor reduceInverted(Tensor image, int channel) {
    return TensorMap.of(color -> Scalars.isZero(color.Get(channel)) ? xFF : RealScalar.ZERO, image, 2);
  }
}
