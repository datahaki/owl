// code by jph
package ch.alpine.owl.bot.psu;

import ch.alpine.sophis.flow.Integrator;
import ch.alpine.sophis.flow.Integrators;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** fifth-order Runge-Kutta formula based on RK4
 * implementation requires 12 flow evaluations
 * 
 * Numerical Recipes 3rd Edition (17.2.3)
 * 
 * class is a simple reference implementation for testing.
 * use RungeKutta45 Integrator for applications */
public enum RungeKutta45Reference implements Integrator {
  INSTANCE;

  private static final Scalar HALF = Rational.HALF;
  private static final Scalar THIRD = Rational.THIRD;
  private static final Scalar SIXTH = Rational.of(1, 6);
  private static final Scalar W1 = Rational.of(-1, 15);
  private static final Scalar W2 = Rational.of(16, 15);

  @Override
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
    Tensor y1 = increment(stateSpaceModel, x, u, h);
    Scalar h2 = h.multiply(Rational.HALF);
    Tensor xm = Integrators.RK4.step(stateSpaceModel, x, u, h2);
    Tensor y2 = Integrators.RK4.step(stateSpaceModel, xm, u, h2).subtract(x);
    Tensor ya = y1.multiply(W1).add(y2.multiply(W2));
    return x.add(ya);
  }

  public static Tensor increment(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
    Tensor k1 = stateSpaceModel.f(x, u).multiply(h); // euler increment
    Tensor k2 = stateSpaceModel.f(x.add(k1.multiply(HALF)), u).multiply(h);
    Tensor k3 = stateSpaceModel.f(x.add(k2.multiply(HALF)), u).multiply(h);
    Tensor k4 = stateSpaceModel.f(x.add(k3), u).multiply(h);
    return k1.add(k4).multiply(SIXTH).add(k2.add(k3).multiply(THIRD));
  }
}
