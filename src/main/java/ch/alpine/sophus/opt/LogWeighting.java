// code by jph
package ch.alpine.sophus.opt;

import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;

public interface LogWeighting {
  /** @param biinvariant
   * @param vectorLogManifold
   * @param variogram
   * @param sequence
   * @return */
  TensorUnaryOperator operator( //
      Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence);

  /** @param biinvariant
   * @param vectorLogManifold
   * @param variogram
   * @param sequence
   * @param values
   * @return */
  TensorScalarFunction function( //
      Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
      Tensor sequence, Tensor values);
}
