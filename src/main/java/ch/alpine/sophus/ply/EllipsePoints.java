// code by jph
package ch.alpine.sophus.ply;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.r2.CirclePoints;

/** @see CirclePoints */
public enum EllipsePoints {
  ;
  /** @param n
   * @param scale vector of length 2
   * @return */
  public static Tensor of(int n, Tensor scale) {
    return Tensor.of(CirclePoints.of(n).stream().map(row -> row.pmul(scale)));
  }

  /** @param n
   * @param width
   * @param height
   * @return */
  public static Tensor of(int n, Scalar width, Scalar height) {
    return of(n, Tensors.of(width, height));
  }
}
