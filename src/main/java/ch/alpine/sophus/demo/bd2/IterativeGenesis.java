// code by jph
package ch.alpine.sophus.demo.bd2;

import java.util.stream.Stream;

import ch.alpine.sophus.gbc.AffineCoordinate;
import ch.alpine.sophus.gbc.d2.Barycenter;
import ch.alpine.sophus.gbc.d2.IterativeCoordinateLevel;
import ch.alpine.sophus.gbc.d2.ThreePointWeighting;
import ch.alpine.sophus.hs.HsDesign;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.sophus.math.Genesis;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.Chop;

/* package */ enum IterativeGenesis {
  MEAN_VALUE(new ThreePointWeighting(Barycenter.MEAN_VALUE)), //
  INVERSE_DISTANCE(AffineCoordinate.INSTANCE), //
  ;

  private final Genesis genesis;

  private IterativeGenesis(Genesis genesis) {
    this.genesis = genesis;
  }

  public TensorScalarFunction with(int max) {
    return new IterativeCoordinateLevel(genesis, Chop._08, max);
  }

  public static TensorUnaryOperator counts(VectorLogManifold vectorLogManifold, Tensor sequence, int max) {
    HsDesign hsDesign = new HsDesign(vectorLogManifold);
    TensorScalarFunction[] array = Stream.of(values()).map(ig -> ig.with(max)).toArray(TensorScalarFunction[]::new);
    return point -> {
      Tensor matrix = hsDesign.matrix(sequence, point);
      return Tensor.of(Stream.of(array).map(ig -> ig.apply(matrix)));
    };
  }
}
