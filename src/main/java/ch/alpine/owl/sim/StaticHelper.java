// code by jph
package ch.alpine.owl.sim;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;

/* package */ enum StaticHelper {
  ;
  /** @param resolution at least 2
   * @return
   * @throws Exception if resolution is less than 2 */
  public static Tensor create(int resolution) {
    Tensor localPoints = Tensors.empty();
    for (Tensor _xn : Subdivide.of(0, 1, resolution - 1)) {
      double xn = ((Scalar) _xn).number().doubleValue();
      double dist = 0.6 + 1.5 * xn + xn * xn;
      for (Tensor _yn : Subdivide.of(-0.5, 0.5, resolution - 1)) {
        double y = ((Scalar) _yn).number().doubleValue();
        Tensor probe = Tensors.vector(dist, y * dist);
        localPoints.append(probe);
      }
    }
    return localPoints;
  }
}
