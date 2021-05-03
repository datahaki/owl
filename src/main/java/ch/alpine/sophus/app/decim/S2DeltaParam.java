// code by jph
package ch.alpine.sophus.app.decim;

import ch.alpine.java.ref.FieldIntegerQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.win.WindowFunctions;

public class S2DeltaParam {
  public Scalar angle = RealScalar.of(0.1);
  public Scalar delta = RealScalar.of(0.1);
  public Scalar noise = RealScalar.of(0.01);
  @FieldIntegerQ
  public Scalar width = RealScalar.of(5);
  public WindowFunctions f_window = WindowFunctions.FLAT_TOP;
  public WindowFunctions s_window = WindowFunctions.HANN;

  public int getWidth() {
    return 2 * Scalars.intValueExact(width) + 1;
  }
}
