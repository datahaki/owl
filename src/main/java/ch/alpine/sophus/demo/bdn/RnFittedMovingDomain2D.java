// code by jph
package ch.alpine.sophus.demo.bdn;

import java.util.stream.IntStream;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.lie.se.RigidMotionFit;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** Reference:
 * "Weighted Averages on Surfaces"
 * by Daniele Panozzo, Ilya Baran, Olga Diamanti, Olga Sorkine-Hornung */
/* package */ class RnFittedMovingDomain2D extends MovingDomain2D {
  /** @param origin
   * @param tensorUnaryOperator
   * @param domain */
  public static MovingDomain2D of(Tensor origin, TensorUnaryOperator tensorUnaryOperator, Tensor domain) {
    return new RnFittedMovingDomain2D(origin, tensorUnaryOperator, domain);
  }

  // ---
  private RnFittedMovingDomain2D(Tensor origin, TensorUnaryOperator tensorUnaryOperator, Tensor domain) {
    super(origin, tensorUnaryOperator, domain);
  }

  @Override // from MovingDomain2D
  public Tensor[][] forward(Tensor target, BiinvariantMean biinvariantMean) {
    int rows = domain.length();
    int cols = Unprotect.dimension1(domain);
    Tensor[][] array = new Tensor[rows][cols];
    Tensor origin = origin();
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy)
        array[cx][cy] = RigidMotionFit.of(origin, target, weights[cx][cy]).apply(domain.get(cx, cy));
    });
    return array;
  }
}
