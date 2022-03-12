// code by jph
package ch.alpine.sophus.ext.api;

import java.util.Objects;

import ch.alpine.sophus.api.Genesis;
import ch.alpine.sophus.gbc.d2.InsideConvexHullCoordinate;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.HsGenesis;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;

public class InsideConvexHullLogWeighting implements LogWeighting {
  private final Genesis genesis;

  public InsideConvexHullLogWeighting(Genesis genesis) {
    this.genesis = Objects.requireNonNull(genesis);
  }

  @Override // from LogWeighting
  public TensorUnaryOperator operator( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence) {
    return HsGenesis.wrap( //
        vectorLogManifold, //
        new InsideConvexHullCoordinate(genesis), //
        sequence);
  }

  @Override // from LogWeighting
  public TensorScalarFunction function( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence, Tensor values) {
    TensorUnaryOperator tensorUnaryOperator = operator(biinvariant, vectorLogManifold, variogram, sequence);
    Objects.requireNonNull(values);
    return point -> (Scalar) tensorUnaryOperator.apply(point).dot(values);
  }
}
