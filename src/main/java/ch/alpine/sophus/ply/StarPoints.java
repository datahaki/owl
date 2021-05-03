// code by jph
package ch.alpine.sophus.ply;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.lie.r2.CirclePoints;

public enum StarPoints {
  ;
  /** the orientation of the points is counter-clockwise.
   * 
   * @param n
   * @param hi
   * @param lo
   * @return (n * 2) x 2 matrix */
  public static Tensor of(int n, Scalar hi, Scalar lo) {
    return Flatten.of(ConstantArray.of(Tensors.of(hi, lo), n)).pmul(CirclePoints.of(n * 2));
  }

  /** @param n
   * @param hi
   * @param lo
   * @return */
  public static Tensor of(int n, Number hi, Number lo) {
    return of(n, RealScalar.of(hi), RealScalar.of(lo));
  }
}
