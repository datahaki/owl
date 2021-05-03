// code by jph
package ch.alpine.owl.bot.rice;

import java.io.Serializable;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

/** single integrator with friction
 * implementation for n-dimensional velocity
 * 
 * The state x (typically velocity) is bounded by "u_max / lambda"
 * where u_max is the maximum control (typically acceleration).
 * 
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * Lipschitz L == lambda */
public class Duncan1StateSpaceModel implements StateSpaceModel, Serializable {
  // ---
  private final Scalar lambda;

  /** @param lambda non-negative friction coefficient typically with unit [s^-1] */
  public Duncan1StateSpaceModel(Scalar lambda) {
    this.lambda = Sign.requirePositiveOrZero(lambda);
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Tensor v = x; // analogous to Duncan2StateSpaceModel
    return u.subtract(v.multiply(lambda));
  }
}
