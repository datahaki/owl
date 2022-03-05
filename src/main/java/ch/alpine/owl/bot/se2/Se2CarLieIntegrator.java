// code by jph
package ch.alpine.owl.bot.se2;

import ch.alpine.sophus.lie.LieIntegrator;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.r2.RotationMatrix;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

/** kinematic car model */
public enum Se2CarLieIntegrator implements LieIntegrator {
  INSTANCE;

  /** function integrates the special case where the y-component of x
   * is constrained to equal 0.
   * 
   * @param g == {px, py, alpha}
   * @param x == {vx, 0, beta}
   * @return g . exp x */
  @Override // from LieIntegrator
  public Tensor spin(Tensor g, Tensor x) {
    Scalar al = g.Get(2);
    Scalar be = x.Get(2);
    if (Scalars.isZero(be))
      return Extract2D.FUNCTION.apply(g).add(RotationMatrix.of(al).dot(Extract2D.FUNCTION.apply(x))).append(al);
    Scalar ra = al.add(be);
    Scalar sd = Sin.FUNCTION.apply(ra).subtract(Sin.FUNCTION.apply(al));
    Scalar cd = Cos.FUNCTION.apply(ra).subtract(Cos.FUNCTION.apply(al));
    Scalar dv = x.Get(0).divide(be);
    return Tensors.of( //
        g.Get(0).add(sd.multiply(dv)), //
        g.Get(1).subtract(cd.multiply(dv)), //
        ra);
  }
}
