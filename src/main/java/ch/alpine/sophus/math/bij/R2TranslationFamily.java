// code by jph
package ch.alpine.sophus.math.bij;

import java.io.Serializable;

import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** the term "family" conveys the meaning that the translation
 * depends on a single parameter, for instance time */
public abstract class R2TranslationFamily implements R2RigidFamily, Serializable {
  @Override // from BijectionFamily
  public final TensorUnaryOperator forward(Scalar scalar) {
    Tensor offset = function_apply(scalar);
    return tensor -> tensor.add(offset);
  }

  @Override // from BijectionFamily
  public final TensorUnaryOperator inverse(Scalar scalar) {
    Tensor offset = function_apply(scalar);
    return tensor -> tensor.subtract(offset);
  }

  @Override // from RigidFamily
  public final Tensor forward_se2(Scalar scalar) {
    return Se2Matrix.of(function_apply(scalar).copy().append(RealScalar.ZERO));
  }

  /** @param scalar
   * @return translation at given scalar parameter */
  public abstract Tensor function_apply(Scalar scalar);
}
