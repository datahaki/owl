// code by jph
package ch.alpine.sophus.ply;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public enum Rocketshape {
  ;
  private static final Tensor POLYGON = //
      Tensors.fromString("{{1.0, 0.0}, {-0.04, 0.14}, {-0.64, 0.4}, {-0.24, 0.0}, {-0.64, -0.4}, {-0.04, -0.14}}");

  /** @param scalar
   * @return spearhead coordinates scaled by given scalar */
  public static Tensor of(Scalar scalar) {
    return POLYGON.multiply(scalar);
  }

  /** @param number
   * @return spearhead coordinates scaled by given number */
  public static Tensor of(Number number) {
    return of(RealScalar.of(number));
  }
}
