// code by jph
package ch.alpine.owl.bot.ip;

import java.io.Serializable;

import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

/** inverted pendulum
 * 
 * from Analysis and Synthesis of Single-Input Single-Output Control Systems, 3rd edition
 * p.13, p.53
 * 
 * @param M mass of cart
 * @param m mass of weight on top of rod
 * @param l length of rod
 * @param g gravitational constant */
record IpStateSpaceModel(Scalar M, Scalar m, Scalar l, Scalar g) implements StateSpaceModel, Serializable {
  public static final StateSpaceModel EXAMPLE = new IpStateSpaceModel( //
      Quantity.of(1, "kg"), Quantity.of(0.1, "kg"), Quantity.of(0.5, "m"), Quantity.of(9.81, "m*s^-2"));

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    // x == [d v a w]
    Scalar v = x.Get(1);
    Scalar a = x.Get(2);
    Scalar w = x.Get(3);
    Scalar F = u.Get(0); // with Newton
    Scalar sa = Sin.FUNCTION.apply(a);
    Scalar ca = Cos.FUNCTION.apply(a);
    Scalar Mpmsasa = M.add(Times.of(m, sa, sa));
    Scalar vd = F.add(Times.of(m, l, w, w, sa)).subtract(Times.of(m, g, ca, sa)).divide(Mpmsasa);
    Scalar wd = Times.of(m.add(M), g, sa).subtract(Times.of(m, l, w, w, ca, sa)).subtract(ca.multiply(F)) //
        .divide(l.multiply(Mpmsasa));
    return Tensors.of(v, vd, w, wd);
  }
}
