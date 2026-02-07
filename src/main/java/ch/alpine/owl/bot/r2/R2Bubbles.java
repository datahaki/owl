// code by jph
package ch.alpine.owl.bot.r2;

import java.io.Serializable;

import ch.alpine.owl.region.ImplicitFunctionRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.Primitives;

/** numerous ~circles of varying size
 * the challenge is to find the path through narrow passages as close as possible to the diagonal
 *
 * suggested values for use are
 * extent = 2
 * root = ( -2.0, -2.0 )
 * goal = ( 2.0, 2.0 ) */
public final class R2Bubbles extends ImplicitFunctionRegion implements Serializable {
  public static final ImplicitFunctionRegion INSTANCE = new R2Bubbles();

  private R2Bubbles() {
    // ---
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor tensor) {
    final double[] data = Primitives.toDoubleArray(tensor);
    double x = data[0];
    double y = data[1];
    double x2 = x * x;
    double y2 = y * y;
    double val = 0;
    val -= Math.cos(3 * Math.PI * x);
    val -= Math.cos(3 * Math.PI * y);
    val += -0.2 * (1 + y2 + x * y + x2);
    return RealScalar.of(-val);
  }
}
