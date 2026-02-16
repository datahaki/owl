// code by jph
package ch.alpine.owl.bot.rn;

import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** Exact integrator for polynomial equations of the model
 * state = {position, velocity}
 * control = {acceleration}
 * 
 * The acceleration is assumed to be constant during integration step.
 * 
 * DSolve[{v'[h] == a, p'[h] == v[h], v[0] == v0, p[0] == p0}, {p[h], v[h]}, h]
 * results in
 * {p[h] -> (a h^2)/2 + p0 + h v0, v[h] -> a h + v0} */
public enum R1Integrator implements Integrator {
  INSTANCE;

  @Override // from Integrator
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
    return direct(x, u.Get(0), h);
  }

  /** @param x vector of the form {position, velocity}
   * @param a acceleration
   * @param h step size
   * @return vector of length 2 */
  public static Tensor direct(Tensor x, Scalar a, Scalar h) {
    Scalar p0 = x.Get(0);
    Scalar v0 = x.Get(1);
    Scalar a2 = a.multiply(Rational.HALF);
    return Tensors.of( //
        a2.multiply(h).add(v0).multiply(h).add(p0), // Polynomial.of(Tensors.of(p0, v0, a2)).apply(h),
        a.multiply(h).add(v0));
  }
}
