// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;

/** region in R^n */
public class HyperplaneRegion extends ImplicitFunctionRegion implements Serializable {
  private static final long serialVersionUID = 2465291970918724766L;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  /** orthogonal is normalized to have Euclidean length 1
   * 
   * @param orthogonal is orthogonal to hyperplane pointing outside
   * @param distanceFromZero needed to reach the region
   * @return */
  public static ImplicitFunctionRegion normalize(Tensor orthogonal, Scalar distanceFromZero) {
    return new HyperplaneRegion(NORMALIZE.apply(orthogonal), distanceFromZero);
  }

  /***************************************************/
  /** orthogonal to hyperplane pointing outside */
  private final Tensor normal;
  private final Scalar distanceFromZero;

  /** @param normal is normal to hyperplane pointing outside
   * @param distanceFromZero needed to reach the region starting from position (0, ..., 0)
   * That means, if distanceFromZero is negative, (0, ..., 0) is inside the region */
  public HyperplaneRegion(Tensor normal, Scalar distanceFromZero) {
    Chop._12.requireClose(Norm._2.ofVector(normal), RealScalar.ONE);
    this.normal = normal;
    this.distanceFromZero = distanceFromZero;
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return distanceFromZero.add(x.dot(normal));
  }
}
