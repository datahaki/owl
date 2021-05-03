// code by jph
package ch.alpine.owl.bot.se2.rl;

import ch.alpine.owl.math.order.VectorLexicographic;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.sca.Chop;

/* package */ enum ScanToState {
  ;
  // TODO possibly switch to empty {} instead of {0}
  public static final Tensor COLLISION = Tensors.vector(0).unmodifiable();

  /** @param range
   * @return {ordering, parity +1 or -1} */
  public static Tensor of(Tensor range) {
    if (Chop.NONE.allZero(range))
      return Tensors.of(COLLISION, RealScalar.ONE);
    Tensor tensor = Tensors.vectorInt(Ordering.DECREASING.of(range));
    Tensor revrse = Reverse.of(tensor);
    return VectorLexicographic.COMPARATOR.compare(tensor, revrse) <= 0 //
        ? Tensors.of(tensor, RealScalar.ONE)
        : Tensors.of(revrse, RealScalar.ONE.negate());
  }
}
