// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.java.ref.FieldIntegerQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class TransitionNdParam {
  private static final Tensor LBOUNDS = Tensors.vector(-5, -5).unmodifiable();
  private static final Tensor UBOUNDS = Tensors.vector(+5, +5).unmodifiable();
  @FieldIntegerQ
  public Scalar points = RealScalar.of(100);
  @FieldIntegerQ
  public Scalar connect = RealScalar.of(3);

  /** @param not used */
  TransitionNdContainer config() {
    return new TransitionNdContainer( //
        LBOUNDS, UBOUNDS, //
        Scalars.intValueExact(points), //
        Scalars.intValueExact(connect));
  }
}
