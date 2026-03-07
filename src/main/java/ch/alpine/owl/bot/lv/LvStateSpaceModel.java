// code by jph
package ch.alpine.owl.bot.lv;

import java.io.Serializable;

import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Entrywise;

/** Lotka-Volterra
 * 
 * https://en.wikipedia.org/wiki/Lotka%E2%80%93Volterra_equations
 * 
 * the stable fix-point of the system is (f1, f0)
 *
 * @param a prey reproduction
 * @param b prey decimation via predators
 * @param c predator natural death rate
 * @param d predator hunting success */
record LvStateSpaceModel(Scalar a, Scalar b, Scalar c, Scalar d) implements StateSpaceModel, Serializable {
  public static final StateSpaceModel EXAMPLE = new LvStateSpaceModel( //
      Quantity.of(1.0, "s^-1"), //
      Quantity.of(0.2, "s^-1"), //
      Quantity.of(0.2, "s^-1"), //
      Quantity.of(0.2, "s^-1"));

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    // hunting u adds to decay of predators:
    Tensor dx = Tensors.of( //
        a.subtract(b.multiply(x.Get(1))), //
        d.multiply(x.Get(0)).subtract(c).subtract(u.Get(0)) //
    );
    return Entrywise.mul().apply(x, dx);
  }
}
