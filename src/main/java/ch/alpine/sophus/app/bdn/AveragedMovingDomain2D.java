// code by jph
package ch.alpine.sophus.app.bdn;

import java.util.stream.IntStream;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** Reference:
 * "Weighted Averages on Surfaces"
 * by Daniele Panozzo, Ilya Baran, Olga Diamanti, Olga Sorkine-Hornung */
/* package */ class AveragedMovingDomain2D extends MovingDomain2D {
  /** @param origin
   * @param tensorUnaryOperator
   * @param domain */
  public static MovingDomain2D of(Tensor origin, TensorUnaryOperator tensorUnaryOperator, Tensor domain) {
    return new AveragedMovingDomain2D(origin, tensorUnaryOperator, domain);
  }

  /***************************************************/
  private AveragedMovingDomain2D(Tensor origin, TensorUnaryOperator tensorUnaryOperator, Tensor domain) {
    super(origin, tensorUnaryOperator, domain);
  }

  @Override // from MovingDomain2D
  public Tensor[][] forward(Tensor target, BiinvariantMean biinvariantMean) {
    int rows = domain.length();
    int cols = Unprotect.dimension1(domain);
    Tensor[][] array = new Tensor[rows][cols];
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy)
        array[cx][cy] = biinvariantMean.mean(target, weights[cx][cy]);
    });
    return array;
  }
}
