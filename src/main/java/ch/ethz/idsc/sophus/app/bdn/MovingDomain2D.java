// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.List;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** Reference:
 * "Weighted Averages on Surfaces"
 * by Daniele Panozzo, Ilya Baran, Olga Diamanti, Olga Sorkine-Hornung */
public abstract class MovingDomain2D {
  private final Tensor origin;
  final Tensor domain;
  final Tensor[][] weights;
  private final Tensor _wgs;

  public MovingDomain2D(Tensor origin, TensorUnaryOperator tensorUnaryOperator, Tensor domain) {
    this.origin = origin;
    this.domain = domain;
    int rows = domain.length();
    int cols = Unprotect.dimension1(domain);
    weights = new Tensor[rows][cols];
    for (int cx = 0; cx < rows; ++cx)
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = domain.get(cx, cy);
        weights[cx][cy] = tensorUnaryOperator.apply(point);
      }
    {
      Tensor wgs = Tensors.matrix((i, j) -> weights[i][j], rows, cols);
      List<Integer> dims = Dimensions.of(wgs);
      _wgs = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
    }
  }

  public final Tensor origin() {
    return origin;
  }

  public final Tensor weights() {
    return _wgs;
  }

  public abstract Tensor[][] forward(Tensor target, BiinvariantMean biinvariantMean);
}
