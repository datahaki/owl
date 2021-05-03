// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.sca.Abs;

/** axis-aligned region of infinity extension in the direction of other axes */
public class FreeBoundedIntervalRegion extends ImplicitFunctionRegion implements Serializable {
  private static final Scalar HALF = RationalScalar.HALF;
  // ---
  private final int index;
  private final Scalar semiwidth;
  private final Scalar center;

  public FreeBoundedIntervalRegion(int index, Scalar lo, Scalar hi) {
    if (Scalars.lessEquals(hi, lo))
      throw TensorRuntimeException.of(lo, hi);
    this.index = index;
    semiwidth = hi.subtract(lo).multiply(HALF);
    center = hi.add(lo).multiply(HALF);
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return semiwidth.subtract(Abs.between(x.Get(index), center));
  }
}
