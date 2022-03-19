// code by jph
package ch.alpine.owl.bot.psu;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.tri.Sin;

/** Pendulum Swing-up state space model
 * 
 * bapaden phd thesis: (5.5.4)
 * 
 * Since the state space model is parameter free, the access
 * to the model is via a singleton instance.
 * 
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * Lipschitz L == 1 */
/* package */ enum PsuStateSpaceModel implements StateSpaceModel {
  INSTANCE;

  /** @param x == {angle, angular rate}
   * @param u == {torque} */
  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    // equation (10)
    // x0' = x1
    // x1' = -sin(x0) + u
    return Tensors.of(x.Get(1), u.Get(0).subtract(Sin.of(x.Get(0))));
  }
}
