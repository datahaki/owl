// code by jph
package ch.alpine.owl.region;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Clip;

/** axis-aligned region of infinity extension in the direction of other axes */
public class FreeBoundedIntervalRegion extends ImplicitFunctionRegion implements Serializable {
  private static final Scalar HALF = RationalScalar.HALF;
  // ---
  private final int index;
  private final Scalar semiwidth;
  private final Scalar center;

  /** @param index non-negative
   * @param clip */
  public FreeBoundedIntervalRegion(int index, Clip clip) {
    this.index = Integers.requirePositiveOrZero(index);
    semiwidth = clip.width().multiply(HALF);
    center = clip.min().add(semiwidth);
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return semiwidth.subtract(Abs.between(x.Get(index), center));
  }
}
