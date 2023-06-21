// code by jph
package ch.alpine.owl.bot.rice;

import java.io.Serializable;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;

/** Important:
 * The use of {@link Duncan2StateSpaceModel} is preferred and
 * supports the use of units.
 * 
 * double integrator with friction
 * Rice2StateSpaceModel is unit less.
 *
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * theory tells that:
 * lipschitz const is 2-norm of 4x4 state space matrix
 * 0 0 1 0
 * 0 0 0 1
 * 0 0 L 0
 * 0 0 0 L
 * where L == -lambda
 * confirmed with Mathematica
 * Lipschitz L == Hypot.of(RealScalar.ONE, lambda);
 * 
 * implementation for n-dimensional position and velocity */
public class Rice2StateSpaceModel implements StateSpaceModel, Serializable {
  /** @param mu
   * @return */
  public static StateSpaceModel of(Scalar mu) {
    return new Rice2StateSpaceModel(Exp.FUNCTION.apply(mu));
  }

  // ---
  private final Scalar lambda;

  /** @param lambda strictly positive friction coefficient */
  private Rice2StateSpaceModel(Scalar lambda) {
    this.lambda = Sign.requirePositive(lambda);
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Tensor v = x.extract(u.length(), x.length());
    return Join.of(v, u.subtract(v).multiply(lambda));
  }
}
