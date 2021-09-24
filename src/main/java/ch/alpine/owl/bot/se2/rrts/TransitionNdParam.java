// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.NdBox;

public class TransitionNdParam {
  private static final NdBox LBOUNDS = NdBox.of(Tensors.vector(-5, -5), Tensors.vector(+5, +5));
  @FieldInteger
  public Scalar points = RealScalar.of(100);
  @FieldInteger
  public Scalar connect = RealScalar.of(3);

  /** @param not used */
  TransitionNdContainer config() {
    return new TransitionNdContainer(LBOUNDS, Scalars.intValueExact(points), Scalars.intValueExact(connect));
  }
}
