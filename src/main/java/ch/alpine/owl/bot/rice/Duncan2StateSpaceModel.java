// code by jph
package ch.alpine.owl.bot.rice;

import java.io.Serializable;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.sca.Sign;

/** double integrator with friction
 * 
 * implementation for n-dimensional velocity
 * 
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * Lipschitz L == Hypot.of(RealScalar.ONE, lambda) */
public class Duncan2StateSpaceModel implements StateSpaceModel, Serializable {
  // ---
  private final Scalar lambda;

  /** @param lambda non-negative friction coefficient typically with unit [s^-1] */
  public Duncan2StateSpaceModel(Scalar lambda) {
    this.lambda = Sign.requirePositiveOrZero(lambda);
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Tensor v = x.extract(u.length(), x.length());
    return Join.of(v, u.subtract(v.multiply(lambda)));
  }
}
